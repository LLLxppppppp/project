import socket
import protocol
import threading
import queue


class Server:
    def __init__(self, host: str = 'localhost', port: int = 23333):
        self.host = host
        self.port = port
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((self.host, self.port))

        self.users = {}
        self.files = {}
        self.history = {}
        self.groups = {}
        self.valid_group_id = 0
        self.valid_group_id_list = []
        # self.server_thread = []
        self.thread_queue = queue.Queue(maxsize=1024)
        self.server_thread_lock = threading.Lock()
        self.terminal = Terminal(self)

    def __add_history(self, target: str, source: str, package: protocol.AppProtocol):
        if target in self.history:
            if source in self.history[target]:
                self.history[target][source].append(package)
            else:
                self.history[target][source] = [package]
        else:
            self.history[target] = {source: [package]}

    def append_group_history(self, group_id: int, package):
        if 'history' in self.groups[group_id]:
            self.groups[group_id]['history'].append(package)
        else:
            self.groups[group_id]['history'] = [package]

    def append_history(self, target: str, source: str, package: protocol.AppProtocol):
        self.__add_history(target, source, package)
        self.__add_history(source, target, package)

    def cut_connect(self, user: str):
        self.users.pop(user)
        if user in self.files:
            self.files.pop(user)
        del_list = []
        for group_id, group in self.groups.items():
            if user in group['members']:
                group['members'].remove(user)
                if len(group['members']) == 0:
                    del_list.append(group_id)
        for group_id in del_list:
            self.groups.pop(group_id)
            self.valid_group_id_list.append(group_id)
        if user in self.history:
            self.history.pop(user)
        del_list = []
        for u, h in self.history.items():
            if user in h:
                del_list.append(u)
        for u in del_list:
            self.history[u].pop(user)

    def run(self):
        print('Server is running...')
        self.server_socket.listen(10)
        self.terminal.start()
        while True:
            client_socket, client_addr = self.server_socket.accept()
            package = protocol.unpack(client_socket)
            if package.type == protocol.CONNECTION_REQUEST and package.data not in self.users:
                print('Connection from {0}, name: {1}.'.format(client_addr, package.data))
                p = protocol.pack(protocol.SERVER_RESPONSE, None)
                client_socket.sendall(p)
                self.users[package.data] = (client_socket, client_addr)
                new_server_thread = ServerThread(client_socket, self, package.data)
                # self.server_thread.append(new_server_thread)
                new_server_thread.start()
                # 有人上线了也给所有人转发在线列表
                user_list = []
                for key in self.users.keys():
                    user_list.append(key)
                self.server_thread_lock.acquire()
                self.thread_queue.put(protocol.AppProtocol(protocol.SERVER_RESPONSE, user_list))
                self.server_thread_lock.release()
            else:
                print('Connection from {0}, refused.'.format(client_addr))
                p = protocol.pack(protocol.CONNECTION_REFUSE, None)
                client_socket.sendall(p)


class ServerThread(threading.Thread):
    def __init__(self, client_socket: socket.socket, server: Server, client_name: str):
        super().__init__()
        self.client_socket = client_socket
        self.server = server
        self.client_name = client_name

    def run(self) -> None:
        while True:
            try:
                package = protocol.unpack(self.client_socket)
            except ConnectionResetError:
                self.server.cut_connect(self.client_name)
                self.server.thread_queue.put(
                    protocol.AppProtocol(protocol.SERVER_RESPONSE, [user for user in self.server.users.keys()]))
                break
            protocol_type, data = package.type, package.data
            # print((protocol_type, data))
            if protocol_type == protocol.GET_USER_LIST:
                user_list = []
                for key in self.server.users.keys():
                    user_list.append(key)
                package = protocol.pack(protocol.SERVER_RESPONSE, user_list)
                self.client_socket.sendall(package)
            elif protocol_type == protocol.GET_MY_GROUPS:
                d = {}
                for group_id, group in self.server.groups.items():
                    if self.client_name in group['members']:
                        d[group_id] = group
                        if 'history' in d[group_id]:
                            d[group_id].pop('history')
                package = protocol.pack(protocol.GET_MY_GROUPS_FINISH, d)
                self.client_socket.sendall(package)
            elif protocol_type == protocol.GET_GROUP_HISTORY:
                group = self.server.groups[data]
                d = []
                history = [] if 'history' not in group else group['history']
                for x in history:
                    d.append((x.type, x.data))
                package = protocol.pack(protocol.GET_GROUP_HISTORY_FINISH, d)
                self.client_socket.sendall(package)
            elif protocol_type == protocol.GET_SOLO_HISTORY:
                if self.client_name in self.server.history and data in self.server.history[self.client_name]:
                    history = self.server.history[self.client_name][data]
                else:
                    history = []
                d = []
                for x in history:
                    d.append((x.type, x.data))
                package = protocol.pack(protocol.GET_SOLO_HISTORY_FINISH, d)
                self.client_socket.sendall(package)
            elif protocol_type == protocol.FILE_PACKAGE:
                self.server.server_thread_lock.acquire()
                if data['source'] not in self.server.files:
                    self.server.files[data['source']] = {data['file_name']: {data['package']: data['content']}}
                else:
                    if data['file_name'] not in self.server.files[data['source']]:
                        self.server.files[data['source']][data['file_name']] = {data['package']: data['content']}
                    else:
                        self.server.files[data['source']][data['file_name']][data['package']] = data['content']
                self.server.server_thread_lock.release()
            elif protocol_type == protocol.GET_FILE or protocol_type == protocol.GET_IMAGE:
                for index, package in self.server.files[data['target']][data['file_name']].items():
                    d = {'target': data['target'], 'file_name': data['file_name'], 'package': index, 'content': package}
                    package = protocol.pack(protocol.FILE_PACKAGE, d)
                    self.client_socket.sendall(package)
                t = None
                if protocol_type == protocol.GET_FILE:
                    t = protocol.GET_FILE_FINISH
                elif protocol_type == protocol.GET_IMAGE:
                    t = protocol.GET_IMAGE_FINISH
                package = protocol.pack(t, {'target': data['target'], 'file_name': data['file_name']})
                self.client_socket.sendall(package)
            elif protocol_type == protocol.CONNECTION_CLOSE:
                self.server.cut_connect(self.client_name)
                package = protocol.pack(protocol.CONNECTION_CLOSE, None)
                self.client_socket.sendall(package)
                user_list = []
                for key in self.server.users.keys():
                    user_list.append(key)
                self.server.server_thread_lock.acquire()
                self.server.thread_queue.put(protocol.AppProtocol(protocol.SERVER_RESPONSE, user_list))
                self.server.server_thread_lock.release()
                print('{0} quit.'.format(self.client_name))
                break
            elif protocol_type in protocol.REDIRECT:
                self.server.server_thread_lock.acquire()
                self.server.thread_queue.put(package)
                self.server.server_thread_lock.release()


class Terminal(threading.Thread):
    def __init__(self, server: Server):
        super().__init__()
        self.server = server

    def run(self) -> None:
        while True:
            self.server.server_thread_lock.acquire()
            flag = self.server.thread_queue.empty()
            self.server.server_thread_lock.release()
            if flag:
                continue
            else:
                self.server.server_thread_lock.acquire()
                package = self.server.thread_queue.get()
                self.server.server_thread_lock.release()
                protocol_type, data = package.type, package.data
                if protocol_type == protocol.SERVER_RESPONSE:
                    p = protocol.pack(protocol.REDIRECT[protocol_type], data)
                    for target in data:
                        target_socket = self.server.users[target][0]
                        target_socket.sendall(p)
                elif protocol_type == protocol.SEND_MESSAGE_SOLO or protocol_type == protocol.SEND_FILE_SOLO or \
                        protocol_type == protocol.SEND_IMAGE_SOLO:
                    p = protocol.pack(protocol.REDIRECT[protocol_type], data)
                    target = data['target']
                    source = data['source']
                    temp_package = package
                    temp_package.type = protocol.REDIRECT[package.type]
                    self.server.append_history(target, source, temp_package)
                    source_socket = self.server.users[data['source']][0]
                    source_socket.sendall(p)
                    target_socket = self.server.users[target][0]
                    target_socket.sendall(p)
                elif protocol_type == protocol.SEND_MESSAGE_MULTI or protocol_type == protocol.SEND_FILE_MULTI or \
                        protocol_type == protocol.SEND_IMAGE_MULTI:
                    p = protocol.pack(protocol.REDIRECT[protocol_type], data)
                    try:
                        group = self.server.groups[data['group_id']]
                    except KeyError:
                        continue
                    members = [member for member in group['members']]
                    temp_package = package
                    temp_package.type = protocol.REDIRECT[package.type]
                    self.server.append_group_history(data['group_id'], temp_package)
                    for member in members:
                        member_socket = self.server.users[member][0]
                        member_socket.sendall(p)
                elif protocol_type == protocol.CREATE_GROUP:
                    p = protocol.pack(protocol.REDIRECT[protocol_type], data)
                    group = data['target']
                    group.append(data['source'])
                    if len(self.server.valid_group_id_list) == 0:
                        valid_id = self.server.valid_group_id
                        self.server.valid_group_id += 1
                    else:
                        valid_id = self.server.valid_group_id_list.pop(0)
                    self.server.groups[valid_id] = {'members': group, 'group_name': data['group_name'], 'history': []}
                    for member in group:
                        member_socket = self.server.users[member][0]
                        member_socket.sendall(p)


if __name__ == '__main__':
    s = Server()
    s.run()
