# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'UI2.ui'
#
# Created by: PyQt5 UI code generator 5.15.2
#
# WARNING: Any manual changes made to this file will be lost when pyuic5 is
# run again.  Do not edit this file unless you know what you are doing.

import sys
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QApplication, QLineEdit, QInputDialog, QAbstractItemView

import client
import protocol
import server

class Ui_main_win(object):
    def setupUi(self, main_win):

        main_win.setObjectName("main_win")
        main_win.resize(472, 353)
        self.centralwidget = QtWidgets.QWidget(main_win)
        self.centralwidget.setObjectName("centralwidget")
        self.horizontalLayoutWidget = QtWidgets.QWidget(self.centralwidget)
        self.horizontalLayoutWidget.setGeometry(QtCore.QRect(0, 0, 471, 351))
        self.horizontalLayoutWidget.setObjectName("horizontalLayoutWidget")
        self.zuida_s = QtWidgets.QHBoxLayout(self.horizontalLayoutWidget)
        self.zuida_s.setContentsMargins(0, 0, 0, 0)
        self.zuida_s.setObjectName("zuida_s")
        self.verticalLayout = QtWidgets.QVBoxLayout()
        self.verticalLayout.setObjectName("verticalLayout")
        self.cida_h1 = QtWidgets.QVBoxLayout()
        self.cida_h1.setObjectName("cida_h1")
        self.msg_listView = QtWidgets.QListWidget(self.horizontalLayoutWidget)
        self.msg_listView.setObjectName("msg_listView")
        self.cida_h1.addWidget(self.msg_listView)
        self.cida_h2_s = QtWidgets.QHBoxLayout()
        self.cida_h2_s.setObjectName("cida_h2_s")
        spacerItem = QtWidgets.QSpacerItem(40, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.cida_h2_s.addItem(spacerItem)
        self.image_up = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.image_up.setObjectName("image_up")
        self.cida_h2_s.addWidget(self.image_up)
        self.dir_up = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.dir_up.setObjectName("dir_up")
        self.cida_h2_s.addWidget(self.dir_up)
        self.cida_h1.addLayout(self.cida_h2_s)
        self.msg_enter = QtWidgets.QTextEdit(self.horizontalLayoutWidget)
        self.msg_enter.setObjectName("msg_enter")
        self.cida_h1.addWidget(self.msg_enter)
        self.cida_h3_h = QtWidgets.QHBoxLayout()
        self.cida_h3_h.setObjectName("cida_h3_h")
        spacerItem1 = QtWidgets.QSpacerItem(40, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.cida_h3_h.addItem(spacerItem1)
        self.msg_up = QtWidgets.QPushButton(self.horizontalLayoutWidget)

        self.msg_up.setObjectName("msg_up")
        self.cida_h3_h.addWidget(self.msg_up)
        self.cida_h1.addLayout(self.cida_h3_h)
        self.cida_h1.setStretch(0, 10)
        self.cida_h1.setStretch(2, 4)
        self.verticalLayout.addLayout(self.cida_h1)
        self.zuida_s.addLayout(self.verticalLayout)
        self.fun_tab = QtWidgets.QTabWidget(self.horizontalLayoutWidget)
        self.fun_tab.setObjectName("fun_tab")
        self.online_member = QtWidgets.QWidget()
        self.online_member.setObjectName("online_member")
        self.verticalLayoutWidget_3 = QtWidgets.QWidget(self.online_member)
        self.verticalLayoutWidget_3.setGeometry(QtCore.QRect(0, 0, 201, 321))
        self.verticalLayoutWidget_3.setObjectName("verticalLayoutWidget_3")
        self.online_member_verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget_3)
        self.online_member_verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.online_member_verticalLayout.setObjectName("online_member_verticalLayout")
        self.listView = QtWidgets.QListView(self.verticalLayoutWidget_3)
        self.listView.setObjectName("listView")
        self.listView.setSelectionMode(QAbstractItemView.ExtendedSelection)
        self.listView.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.online_member_verticalLayout.addWidget(self.listView)
        self.fun_tab.addTab(self.online_member, "")
        self.my_group = QtWidgets.QWidget()
        self.my_group.setObjectName("my_group")
        self.verticalLayoutWidget_4 = QtWidgets.QWidget(self.my_group)
        self.verticalLayoutWidget_4.setGeometry(QtCore.QRect(0, 0, 201, 321))
        self.verticalLayoutWidget_4.setObjectName("verticalLayoutWidget_4")
        self.my_group_verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget_4)
        self.my_group_verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.my_group_verticalLayout.setObjectName("my_group_verticalLayout")
        self.my_group_listView = QtWidgets.QListView(self.verticalLayoutWidget_4)
        self.my_group_listView.setObjectName("my_group_listView")
        self.my_group_verticalLayout.addWidget(self.my_group_listView)
        self.cre_group = QtWidgets.QPushButton(self.verticalLayoutWidget_4)
        self.cre_group.setObjectName("cre_group")
        self.my_group_verticalLayout.addWidget(self.cre_group)
        self.fun_tab.addTab(self.my_group, "")
        self.group_dir = QtWidgets.QWidget()
        self.group_dir.setObjectName("group_dir")
        self.verticalLayoutWidget_5 = QtWidgets.QWidget(self.group_dir)
        self.verticalLayoutWidget_5.setGeometry(QtCore.QRect(0, 0, 211, 321))
        self.verticalLayoutWidget_5.setObjectName("verticalLayoutWidget_5")
        self.group_dir_verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget_5)
        self.group_dir_verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.group_dir_verticalLayout.setObjectName("group_dir_verticalLayout")
        self.group_dir_listView = QtWidgets.QListView(self.verticalLayoutWidget_5)
        self.group_dir_listView.setObjectName("group_dir_listView")
        self.group_dir_verticalLayout.addWidget(self.group_dir_listView)
        self.download_dir = QtWidgets.QPushButton(self.verticalLayoutWidget_5)
        self.download_dir.setObjectName("download_dir")
        self.group_dir_verticalLayout.addWidget(self.download_dir)
        self.fun_tab.addTab(self.group_dir, "")
        self.zuida_s.addWidget(self.fun_tab)
        self.zuida_s.setStretch(0, 6)
        main_win.setCentralWidget(self.centralwidget)

        self.retranslateUi(main_win)
        self.fun_tab.setCurrentIndex(0)
        QtCore.QMetaObject.connectSlotsByName(main_win)

    def retranslateUi(self, main_win):
        _translate = QtCore.QCoreApplication.translate
        main_win.setWindowTitle(_translate("main_win", "多人聊天室"))
        self.image_up.setText(_translate("main_win", "图片发送"))
        self.dir_up.setText(_translate("main_win", "文件上传"))
        self.msg_up.setText(_translate("main_win", "发送消息"))
        self.fun_tab.setTabText(self.fun_tab.indexOf(self.online_member), _translate("main_win", "在线成员"))
        self.cre_group.setText(_translate("main_win", "创建群聊"))
        self.fun_tab.setTabText(self.fun_tab.indexOf(self.my_group), _translate("main_win", "我的群聊"))
        self.download_dir.setText(_translate("main_win", "下载文件"))
        self.fun_tab.setTabText(self.fun_tab.indexOf(self.group_dir), _translate("main_win", "群文件"))

