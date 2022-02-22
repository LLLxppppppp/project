import pickle
import struct
import socket

# Defines for protocol
"""****** Client GUI不用处理的消息类型 ******"""
CONNECTION_REQUEST = 100
CONNECTION_REFUSE = 666
CONNECTION_CLOSE = 886
FILE_PACKAGE = 101
"""**************************************"""

"""****** Client GUI可能需要发送的类型 ******"""
GET_USER_LIST = 200
GET_FILE = 201
GET_IMAGE = 202
GET_MY_GROUPS = 203
GET_GROUP_HISTORY = 204
GET_SOLO_HISTORY = 205

SEND_MESSAGE_SOLO = 300
SEND_MESSAGE_MULTI = 301
SEND_FILE_SOLO = 302
SEND_FILE_MULTI = 303
SEND_IMAGE_SOLO = 304
SEND_IMAGE_MULTI = 305
CREATE_GROUP = 306
"""**************************************"""

"""****** Client GUI可能会收到的类型 ******"""
RECV_MESSAGE_SOLO = 400  # data = {'target': str=接收方, 'source': str=发送方, 'message': str=消息, 'time': str=发送时间}
RECV_MESSAGE_MULTI = 401  # data = {'target': list=接收方列表, 'source': str=发送方, 'message': str=消息, 'time': str=发送时间}
RECV_FILE_SOLO = 402  # data = {'target': str=接收方, 'source': str=发送方, 'file_name': str=文件名, 'file_size': int=文件大小,# 'time': str=发送时间}
RECV_FILE_MULTI = 403  # data = {'target': list=接收方列表, 'source': str=发送方, 'file_name': str=文件名, 'file_size': int=文件大小,# 'time': str=发送时间}
RECV_IMAGE_SOLO = 404  # data = {'target': str=接收方, 'source': str=发送方, 'file_name': str=图片名, 'file_size': int=图片大小,# 'time': str=发送时间}
RECV_IMAGE_MULTI = 405  # data = {'target': list=接收方列表, 'source': str=发送方, 'file_name': str=图片名, 'file_size': int=图片大小,# 'time': str=发送时间}
BE_INCLUDED_GROUP = 406  # data = {'target': list=参与群聊人员, 'source': str=群聊发起人, 'group_name': str=群聊名称}

# 表示收到了一个在线成员列表
SERVER_RESPONSE = 500  # data = [str=在线成员1, str=在线成员2, ...]
# 表示自己发送的 GET_FILE (从服务器下载某人的某文件) 已经成功
GET_FILE_FINISH = 501  # data = {'target': str=文件发送者, 'file_name': 文件名称}
# 表示自己发送的 GET_IMAGE (从服务器下载某人的某文件) 已经成功
GET_IMAGE_FINISH = 502  # data = {'target': str=图片发送者, 'file_name': 图片名称}
# 表示成功获取到自己参与的Groups
GET_MY_GROUPS_FINISH = 503  # data = {group_id1(int): dict={'members': list, 'group_name': str}, group_id2(int): ...}
# 收到服务器反馈的目标群聊的聊天记录
GET_GROUP_HISTORY_FINISH = 504  # data = [(protocol_type, data), (protocol_type, data), (protocol_type, data) ...]
# 收到服务器反馈的目标的聊天记录
GET_SOLO_HISTORY_FINISH = 505  # data = [(protocol_type, data), (protocol_type, data), (protocol_type, data) ...]
"""**************************************"""

REDIRECT = {SEND_MESSAGE_SOLO: RECV_MESSAGE_SOLO, SEND_MESSAGE_MULTI: RECV_MESSAGE_MULTI,
            SEND_FILE_SOLO: RECV_FILE_SOLO, SEND_FILE_MULTI: RECV_FILE_MULTI,
            SEND_IMAGE_SOLO: RECV_IMAGE_SOLO, SEND_IMAGE_MULTI: RECV_IMAGE_MULTI,
            CREATE_GROUP: BE_INCLUDED_GROUP, SERVER_RESPONSE: SERVER_RESPONSE}


class AppProtocol:
    def __init__(self, type: int, data):
        self.type = type
        self.data = data


def pack(ptype: int, data):
    p = AppProtocol(ptype, data)
    p = pickle.dumps(p)
    p_len = struct.pack('I', len(p))
    p = p_len + p
    return p


def unpack(soc: socket.socket):
    p_len = soc.recv(4)
    p_len = struct.unpack('I', p_len)
    p = soc.recv(p_len[0])
    p = pickle.loads(p)
    return p


class NameBeenUsedError(RuntimeError):
    def __init__(self, arg=()):
        self.args = arg


class UnknownServerError(RuntimeError):
    def __init__(self, arg=()):
        self.args = arg
