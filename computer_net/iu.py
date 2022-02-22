import sys

from PyQt5 import QtWidgets
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
from PyQt5 import QtCore
from PyQt5.QtCore import*
import client
import protocol
import server
from ListItem import ListItem_UI
import os

import UI2
class my_win(UI2.Ui_main_win, QMainWindow):
    _RecvCallBack = QtCore.pyqtSignal(int, dict)
    def __init__(self):
        super().__init__()
        self.initUI()  # 界面绘制交给InitUi方法
        self.mem_list = []
        self.download_file = []
        self.download_image = []
        self.msglist = []
        self.multi_msg = []
        self.solo_file = []
        self.multi_file = []
        self.solo_image = []
        self.multi_image = []

        self.target = None

        self.groups = {}

        self._RecvCallBack.connect(self.myFunc)
        # 给自己取一个名字
        name, okPressed = QInputDialog.getText(None, "Get text", "Your name:", QLineEdit.Normal, "")
        self.client = client.Client(name, self.RecvCallBack)

        self.user_status = {self.client.name: 0}

        #绑定事件

        self.msg_up.clicked.connect(self.op_send_msg)
        self.image_up.clicked.connect(self.op_send_image)
        self.dir_up.clicked.connect(self.op_send_file)
        self.cre_group.clicked.connect(self.create_group)

        self.listView.clicked.connect(self.memberClick)
        self.my_group_listView.clicked.connect(self.groupClick)

    def memberClick(self):
        select = self.listView.selectedIndexes()
        idx = None if len(select) == 0 else None if select[0].row() == 0 else select[0].row()
        if idx and len(select) == 1:
            self.target = self.MemberModel.stringList()[idx].split(' ')[0]
            self.msg_listView.clear()
            self.client.get_solo_history(self.MemberModel.stringList()[idx].split(' ')[0])

            self.user_status[self.MemberModel.stringList()[idx].split(' ')[0]] = 3
            for k, v in self.user_status.items():
                if v == 3 and k != self.MemberModel.stringList()[idx].split(' ')[0]:
                    self.user_status[k] = 1
            self.MemberModel = QStringListModel(self.transfer())
            self.listView.setModel(self.MemberModel)
            self.listView.setCurrentIndex(self.MemberModel.index(select[0].row(), select[0].column()))

    def groupClick(self):
        select = self.my_group_listView.selectedIndexes()
        group_id = None
        groups = list(self.groups.keys())
        if len(select) == 1:
            row = select[0].row()
            group_id = groups[row]
        self.msg_listView.clear()
        self.client.get_group_history(group_id)

    def transfer(self):
        l = []
        for k, v in self.user_status.items():
            if v == 0:
                l.append(k + ' [自己]')
            elif v == 1:
                l.append(k)
            elif v == 2:
                l.append(k + ' (*)')
            elif v == 3:
                l.append(k + ' [选中]')
        return l

    def RecvCallBack(self, ptype, data):
        self._RecvCallBack.emit(ptype, data)


    def resizeEvent(self, a0=None):  # 窗口size更改，调整布局新尺寸
        self.horizontalLayoutWidget.setFixedSize(self.centralwidget.size())


    def addMsgListItem(self, sender, Msg, time):
        LstItmWidget = MsgListItem(sender, Msg, time)
        item = QListWidgetItem()

        LstItmWidget.setAttribute(Qt.WA_DontShowOnScreen)
        LstItmWidget.show()
        LstItmWidget.hide()
        LstItmWidget.setAttribute(Qt.WA_DontShowOnScreen, False)

        item.setSizeHint(LstItmWidget.size())
        self.msg_listView.addItem(item)  # 设置子控件
        self.msg_listView.setItemWidget(item, LstItmWidget)


    def myFunc(self, ptype, data):
        data = data['data']
        print(data, ptype, '11111111111111--')
        #成员显示
        if ptype == protocol.SERVER_RESPONSE:
            self.nameList = [name for name in data if name != self.client.name]
            self.nameList.insert(0, self.client.name)

            for x in data:
                if x != self.client.name:
                    if x not in self.user_status:
                        self.user_status[x] = 1
            remove = []
            for k in self.user_status.keys():
                if k not in data:
                    remove.append(k)
            for k in remove:
                self.user_status.pop(k)

            SLM = QStringListModel(self.transfer())
            self.MemberModel = SLM
            self.listView.setModel(SLM)
        #群聊显示
        if ptype == protocol.GET_MY_GROUPS_FINISH:
            self.group_name_list = []
            for group_id, group in data.items():
                group_name = group['group_name']
                members = group['members']
                tmp = '['
                if len(members) <= 3:
                    for x in members:
                        tmp += x + ','
                else:
                    for i in range(0, 3):
                        tmp += members[i] + ','
                tmp += ']'
                self.groups[group_id] = group_name + tmp
                self.group_name_list.append(self.groups[group_id])
            tempList = self.group_name_list
            slm = QStringListModel(tempList)
           # print(258258)
            print(tempList)
            self.GroupModel = slm
            self.my_group_listView.setModel(slm)
        #文件显示
        elif ptype == protocol.GET_FILE_FINISH:
            if self.target == data['target'] or self.client.name == data['target']:
                fileHtml = '有一份新的文件' + data['file_name']
                self.addMsgListItem(data['target'], fileHtml, "")
            else:
                self.user_status[data['target']] = 2
                self.MemberModel = QStringListModel(self.transfer())
                self.listView.setModel(self.MemberModel)

        #图片显示
        elif ptype == protocol.GET_IMAGE_FINISH:
            if self.target == data['target'] or self.client.name == data['target']:
                imgHtml = '<img src="%s">' % (os.path.join('image', data['file_name']))  # width="60" height="60"
                self.addMsgListItem(data['target'], imgHtml, "")
            else:
                self.user_status[data['target']] = 2
                self.MemberModel = QStringListModel(self.transfer())
                self.listView.setModel(self.MemberModel)
        #消息显示
        #单条消息显示
        elif ptype == protocol.RECV_MESSAGE_SOLO:
            # self.client.get_solo_history(data['target'])
            # self.solo_msg = data
            # selected_user = None
            # select = self.listView.selectedIndexes()
            # idx = None if len(select) == 0 else None if select[0].row() == 0 else select[0].row()
            # if idx:
            #     selected_user = self.MemberModel.stringList()[idx].split(' ')[0]

            if self.target == data['source'] or self.client.name == data['source']:
                self.addMsgListItem(data['source'], data['message'], data['time'])
            else:
                self.user_status[data['source']] = 2
                self.MemberModel = QStringListModel(self.transfer())
                self.listView.setModel(self.MemberModel)

        #显示单人聊天记录
        elif ptype == protocol.GET_SOLO_HISTORY_FINISH:
            for x in data:
                t, d = x[0], x[1]
                temp = None
                if self.target == d['target']:
                    temp = self.client.name
                else:
                    temp = self.target
                if t == protocol.RECV_IMAGE_SOLO:
                    imgHtml = '<img src="%s">' % (os.path.join('image', d['file_name']))  # width="60" height="60"
                    self.addMsgListItem(temp, imgHtml, "")
                elif t == protocol.RECV_FILE_SOLO:
                    fileHtml = '有一份新的文件' + d['file_name']
                    self.addMsgListItem(temp, fileHtml, "")
                else:
                    self.addMsgListItem(d['source'], d['message'], d['time'])

        #多条消息显示
        elif ptype == protocol.RECV_MESSAGE_MULTI:
            # self.client.get_solo_history()
            self.multi_msg = data
            self.addMsgListItem(data['source'], data['message'], data['time'])

        #显示群聊记录
        elif ptype == protocol.GET_GROUP_HISTORY_FINISH:
            for x in data:
                t, d = x[0], x[1]
                if t == protocol.RECV_IMAGE_MULTI:
                    imgHtml = '<img src="%s">' % (os.path.join('image', d['file_name']))  # width="60" height="60"
                    self.addMsgListItem(d['source'], imgHtml, "")
                elif t == protocol.RECV_FILE_MULTI:
                    fileHtml = '有一份新的文件' + d['file_name']
                    self.addMsgListItem(d['source'], fileHtml, "")
                else:
                    self.addMsgListItem(d['source'], d['message'], d['time'])
        #单个文件下载
        elif ptype == protocol.RECV_FILE_SOLO:
            self.solo_file = data
            self.client.download_file(data['source'], data['file_name'], 'download')
        #多个文件显示
        elif ptype == protocol.RECV_FILE_MULTI:
            self.multi_file = data
            self.client.download_file(data['source'], data['file_name'], 'download')


        #单个图片下载
        elif ptype == protocol.RECV_IMAGE_SOLO:
            self.solo_image = data
            self.client.download_image(data['source'], data['file_name'], 'image')


        #多个图片显示
        elif ptype == protocol.RECV_IMAGE_MULTI:
            self.multi_image = data
            self.client.download_image(data['source'], data['file_name'], 'image')

        #得到群
        elif ptype == protocol.BE_INCLUDED_GROUP:
            self.client.get_my_groups()
            select = self.my_group_listView.selectedIndexes()
            group_id = None
            groups = list(self.groups.keys())
            if len(select) == 1:
                row = select[0].row()
                group_id = groups[row]
            if group_id:
                self.client.get_group_history(group_id)


    def send_msg_solo(self):
        message = str(self.msg_enter.toPlainText())
        if self.target is not None:
            self.client.send_message_solo(self.target, message)
            self.msg_enter.clear()
        # select = self.listView.selectedIndexes()
        # if len(select) == 1:
        #     row = select[0].row()
        #     if row > 0:
        #         target = self.MemberModel.stringList()[row].split(' ')[0]
        #         self.client.send_message_solo(target, message)

    def send_msg_multi(self):
        message = str(self.msg_enter.toPlainText())
        select = self.my_group_listView.selectedIndexes()
        group_id = None
        groups = list(self.groups.keys())
        if len(select) == 1:
            row = select[0].row()
            group_id = groups[row]
        # 'group_name[members]'
        # target = target.split('[')[0]
        # for k, v in self.groups.items():
        #     if v == target:
        #         group_id = k
        self.client.send_message_multi(group_id, message)

    def op_send_msg(self):
        if self.fun_tab.currentIndex() == 0:
            self.send_msg_solo()
        if self.fun_tab.currentIndex() == 1:
            self.send_msg_multi()

    def send_solo_image(self):
        fileName, filetype = QFileDialog.getOpenFileName(None,
                                                         "选取文件",
                                                         "./",
                                                         "All Files (*.jpg;*.png;*.bmp;*.gif);;jpg Files (*.jpg);;png Files (*.png);;bmp Files (*.bmp);;gif Files (*.gif)")
        if fileName == None or fileName == "":
            return

        select = self.listView.selectedIndexes()
        if len(select) == 1:
            row = select[0].row()
            if row > 0:
                target = self.MemberModel.stringList()[row].split(' ')[0]
                self.client.send_image_solo(target, fileName)


#多发图片，未完成编辑
    def send_multi_image(self):
        fileName, filetype = QFileDialog.getOpenFileName(None,
                                                         "选取文件",
                                                         "./",
                                                         "All Files (*.jpg;*.png;*.bmp;*.gif);;jpg Files (*.jpg);;png Files (*.png);;bmp Files (*.bmp);;gif Files (*.gif)")
        if fileName == None or fileName == "":
            return

        select = self.my_group_listView.selectedIndexes()
        group_id = None
        groups = list(self.groups.keys())
        if len(select) == 1:
            row = select[0].row()
            group_id = groups[row]
        # 'group_name[members]'
        # target = target.split('[')[0]
        # for k, v in self.groups.items():
        #     if v == target:
        #         group_id = k
        self.client.send_image_multi(group_id, fileName)

    def op_send_image(self):
        if self.fun_tab.currentIndex() == 0:
            self.send_solo_image()
        if self.fun_tab.currentIndex() == 1:
            self.send_multi_image()
#单发文件，未完成编辑
    def send_solo_file(self):
        fileName, filetype = QFileDialog.getOpenFileName(None,
                                                     "选取文件",
                                                     "./",
                                                     "All Files (*.*)")
        if fileName == None or fileName == "":
            return

        select = self.listView.selectedIndexes()
        if len(select) == 1:
            row = select[0].row()
            if row > 0:
                target = self.MemberModel.stringList()[row].split(' ')[0]
                self.client.send_file_solo(target, fileName)

# 多发文件，未完成编辑
    def send_multi_file(self):
        fileName, filetype = QFileDialog.getOpenFileName(None,
                                                     "选取文件",
                                                     "./",
                                                     "All Files (*.*)")
        if fileName == None or fileName == "":
            return
        select = self.my_group_listView.selectedIndexes()
        group_id = None
        groups = list(self.groups.keys())
        if len(select) == 1:
            row = select[0].row()
            group_id = groups[row]
        # 'group_name[members]'
        # target = target.split('[')[0]
        # for k, v in self.groups.items():
        #     if v == target:
        #         group_id = k
        self.client.send_file_multi(group_id, fileName)
    def op_send_file(self):
        if self.fun_tab.currentIndex() == 0:
            self.send_solo_file()
        if self.fun_tab.currentIndex() == 1:
            self.send_multi_file()


    def create_group(self):
        select = self.listView.selectedIndexes()
        group_name, okPressed = QInputDialog.getText(None, "Get text", "group name:", QLineEdit.Normal, "")
        grouplist = []
        if len(select) > 0:
            for it in select:
                print(self.nameList, 333333333)
                name = self.nameList[it.row()]
                if name != self.client.name:
                    grouplist.append(name)

        self.client.create_group(grouplist, group_name)


    def initUI(self):
        self.setupUi(self)

    def closeEvent(self, event):
        reply = QtWidgets.QMessageBox.Yes
        # reply = QtWidgets.QMessageBox.question(None,
        #                                        '本程序',
        #                                        "是否要退出程序？",
        #                                        QtWidgets.QMessageBox.Yes | QtWidgets.QMessageBox.No,
        #                                        QtWidgets.QMessageBox.No)
        if reply == QtWidgets.QMessageBox.Yes:
            self.client.close()
            event.accept()
        else:
            event.ignore()


class MsgListItem(QWidget, ListItem_UI):
    # 聊天列表的item布局
    def __init__(self, sender, msg, time):
        super(MsgListItem, self).__init__(None)
        self.setupUi(sender, msg, time)


if __name__ == '__main__':
    # 创建应用程序和对象
    app = QApplication(sys.argv)
    mw = my_win()    # 登陆界面
    mw.show()
    sys.exit(app.exec_()) #结束一个应用程序，使应用程序在主循环中退出

