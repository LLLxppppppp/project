import socket
import protocol
import threading
import time
import os


class Client:
    def __init__(self, name: str, func):
        # Init
        self.name = name
        self.call_back = func
        self.server_host = 'localhost'
        self.server_port = 23333
        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  #ipv4 数据流形式
        # Try to connect
        self.connect_to_server()
        # Thread
        self.client_thread_lock = threading.Lock()
        # self.client_queue = queue.Queue(maxsize=1024)
        self.client_files = {}
        self.client_thread = ClientThread(self)
        self.client_thread.start()

    def connect_to_server(self):
        self.client_socket.connect((self.server_host, self.server_port))
        package = protocol.pack(protocol.CONNECTION_REQUEST, self.name)
        self.client_socket.sendall(package)
        back = protocol.unpack(self.client_socket)
        if back.type == protocol.CONNECTION_REFUSE:
            self.client_socket.close()
            raise protocol.NameBeenUsedError()
        elif back.type == protocol.SERVER_RESPONSE:
            print('Debug: connection success')

    # def has_response(self) -> bool:
    #     return not self.client_queue.empty()
    #
    # def get_response(self) -> protocol.AppProtocol:
    #     self.client_thread_lock.acquire()
    #     package = self.client_queue.get()
    #     self.client_thread_lock.release()
    #     return package

    # 获取成员列表
    def get_user_list(self):
        package = protocol.pack(protocol.GET_USER_LIST, None)
        self.client_socket.sendall(package)

    # 关闭客户端，程序结束前调用
    def close(self):
        package = protocol.pack(protocol.CONNECTION_CLOSE, self.name)
        self.client_socket.sendall(package)

    def __send_message(self, arg, message: str, method: str):
        protocol_type = None
        data = None
        if method == 'solo':
            data = {'target': arg, 'source': self.name, 'message': message,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_MESSAGE_SOLO
        elif method == 'multi':
            data = {'group_id': arg, 'source': self.name, 'message': message,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_MESSAGE_MULTI
        package = protocol.pack(protocol_type, data)
        self.client_socket.sendall(package)

    # 单发消息给某人
    # @param target 接收方
    # @param message 消息
    def send_message_solo(self, target: str, message: str):
        self.__send_message(target, message, 'solo')

    # 群发消息给某些人
    # @param target 接收方列表
    # @param message 消息
    def send_message_multi(self, group_id: int, message: str):
        self.__send_message(group_id, message, 'multi')

    def __send_file(self, arg, file_path, method: str):
        file_name = os.path.basename(file_path)
        size = file_size = os.stat(file_path).st_size
        buffer = 2048
        pkg_num = int(file_size / buffer) + 1
        with open(file_path, 'rb') as file:
            for i in range(0, pkg_num):
                if file_size > buffer:
                    content = file.read(buffer)
                    file_size -= buffer
                else:
                    content = file.read(file_size)
                data = {'source': self.name, 'content': content, 'package': i, 'file_name': file_name}
                package = protocol.pack(protocol.FILE_PACKAGE, data)
                self.client_socket.sendall(package)
        data = None
        protocol_type = None
        if method == 'solo':
            data = {'target': arg, 'source': self.name, 'file_name': file_name, 'file_size': size,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_FILE_SOLO
        elif method == 'multi':
            data = {'group_id': arg, 'source': self.name, 'file_name': file_name, 'file_size': size,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_FILE_MULTI
        elif method == 'solo_image':
            data = {'target': arg, 'source': self.name, 'file_name': file_name, 'file_size': size,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_IMAGE_SOLO
        elif method == 'multi_image':
            data = {'group_id': arg, 'source': self.name, 'file_name': file_name, 'file_size': size,
                    'time': time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(time.time()))}
            protocol_type = protocol.SEND_IMAGE_MULTI
        package = protocol.pack(protocol_type, data)
        self.client_socket.sendall(package)

    # 单发文件给某人
    # @param target 接收方
    # @param file_path 要发送的文件路径
    def send_file_solo(self, target: str, file_path: str):
        self.__send_file(target, file_path, 'solo')

    # 群发文件给某些人
    # @param target 接收方列表
    # @param file_path 要发送的文件路径
    def send_file_multi(self, group_id: int, file_path: str):
        self.__send_file(group_id, file_path, 'multi')

    # 单发图片给某人
    # @param target 接收方
    # @param file_path 要发送的图片路径
    def send_image_solo(self, target: str, file_path: str):
        self.__send_file(target, file_path, 'solo_image')

    # 群发图片给某些人
    # @param target 接收方列表
    # @param file_path 要发送的图片路径
    def send_image_multi(self, group_id: int, file_path: str):
        self.__send_file(group_id, file_path, 'multi_image')

    def __download(self, target: str, file_name: str, save_path: str, method):
        self.client_thread.recv_file_path = save_path
        if self.client_thread.recv_file_path[-1] != '/':
            self.client_thread.recv_file_path += '/'
        protocol_type = None
        if method == 'file':
            protocol_type = protocol.GET_FILE
        elif method == 'image':
            protocol_type = protocol.GET_IMAGE
        package = protocol.pack(protocol_type, {'target': target, 'file_name': file_name})
        self.client_socket.sendall(package)

    # 从服务器下载某个文件
    # @param target 要下载的文件来自谁
    # @param file_name 要下载的文件的名称
    # @param save_path 下载的文件存放路径(不用带文件名)
    def download_file(self, target: str, file_name: str, save_path: str):
        self.__download(target, file_name, save_path, method='file')

    # 从服务器下载某个图片
    # @param target 要下载的图片来自谁
    # @param file_name 要下载的图片的名称
    # @param save_path 下载的图片存放路径(不用带文件名)
    def download_image(self, target: str, file_name: str, save_path: str):
        self.__download(target, file_name, save_path, method='image')

    # 创建群聊
    # @param target 群聊成员列表(除了自己）
    # @param group_name 群聊名称
    def create_group(self, target: list, group_name: str = ''):
        d = {'target': target, 'source': self.name, 'group_name': group_name}
        p = protocol.pack(protocol.CREATE_GROUP, d)
        self.client_socket.sendall(p)

    # 获取自己参与的群聊
    def get_my_groups(self):
        p = protocol.pack(protocol.GET_MY_GROUPS, None)
        self.client_socket.sendall(p)

    # 获取某群聊的历史记录
    # @param group_id 群聊ID
    def get_group_history(self, group_id: int):
        p = protocol.pack(protocol.GET_GROUP_HISTORY, group_id)
        self.client_socket.sendall(p)

    # 获取单人聊天历史记录
    # @param target 聊天目标
    def get_solo_history(self, target: str):
        p = protocol.pack(protocol.GET_SOLO_HISTORY, target)
        self.client_socket.sendall(p)


class ClientThread(threading.Thread):
    def __init__(self, client: Client):
        super().__init__()
        self.client = client
        self.recv_file_path = './'
        self.recv_image_path = './'

    def run(self) -> None:
        while True:
            try:
                package = protocol.unpack(self.client.client_socket)
            except ConnectionResetError:
                break
            protocol_type, data = package.type, package.data
            print((protocol_type, data))
            if protocol_type == protocol.CONNECTION_CLOSE:
                self.client.client_thread_lock.acquire()
                self.client.client_socket.close()
                self.client.client_thread_lock.release()
                break
            elif protocol_type == protocol.FILE_PACKAGE:
                self.client.client_thread_lock.acquire()
                if data['target'] not in self.client.client_files:
                    self.client.client_files[data['target']] = {data['file_name']: {data['package']: data['content']}}
                else:
                    if data['file_name'] not in self.client.client_files[data['target']]:
                        self.client.client_files[data['target']][data['file_name']] = {data['package']: data['content']}
                    else:
                        self.client.client_files[data['target']][data['file_name']][data['package']] = data['content']
                self.client.client_thread_lock.release()
                continue
            elif protocol_type == protocol.GET_FILE_FINISH or protocol_type == protocol.GET_IMAGE_FINISH:
                target = data['target']
                file_name = data['file_name']
                self.client.client_thread_lock.acquire()
                if not os.path.exists(self.recv_file_path):
                    os.mkdir(self.recv_file_path)
                with open(self.recv_file_path + file_name, 'wb') as file:
                    for i in range(0, len(self.client.client_files[target][file_name])):
                        file.write(self.client.client_files[target][file_name][i])
                self.client.client_thread_lock.release()
            self.client.client_thread_lock.acquire()
            self.client.call_back(protocol_type, {'data': data})
            # self.client.client_queue.put(package)
            self.client.client_thread_lock.release()


if __name__ == '__main__':
    c = Client('User1', lambda t, d: None)
    time.sleep(0.05)
    c.send_message_solo('User1', 'send message solo')
    time.sleep(0.05)
    c.create_group([], 'Test group')
    time.sleep(0.05)
    c.get_my_groups()
    time.sleep(0.05)
    c.send_message_multi(0, 'send message multi 1')
    time.sleep(0.05)
    c.send_message_multi(0, 'send message multi 2')
    time.sleep(0.05)
    c.send_message_multi(0, 'send message multi 3')
    time.sleep(0.05)
    c.get_group_history(0)
    time.sleep(0.05)
    filepath = 'C:\\Users\\Lxp\\Desktop\\test_file.txt'
    c.send_image_solo('User1', filepath)
    time.sleep(0.05)
    c.download_image('User1', 'test_file.txt', './recv')
    time.sleep(0.05)
    c.close()
