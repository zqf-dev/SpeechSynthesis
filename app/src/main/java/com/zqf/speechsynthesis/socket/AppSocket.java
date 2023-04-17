package com.zqf.speechsynthesis.socket;

import android.util.Log;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class AppSocket {
    private static final String TAG = AppSocket.class.getName();
    private static ConnectionInfo info;
    private static IConnectionManager manager;

    public static void connectServer() {
        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        info = new ConnectionInfo("10.10.15.17", 23);
        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        manager = OkSocket.open(info);
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Log.i(TAG, "连接成功");
                OkSocket.open(info)
                        .getPulseManager()
                        .setPulseSendable(new IPulseSendable() {
                            @Override
                            public byte[] parse() {
                                byte[] body = "pause".getBytes(Charset.defaultCharset()); // 心跳数据
                                ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
                                bb.order(ByteOrder.BIG_ENDIAN);
                                bb.putInt(body.length);
                                bb.put(body);
                                return bb.array();
                            }
                        }).pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
            }

            @Override
            public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(info, action, e);
                Log.i(TAG, "断开连接");
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(info, action, data);
                Log.i(TAG, "接收的信息info：");
            }

            @Override
            public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                super.onSocketConnectionFailed(info, action, e);
                Log.i(TAG, "客户端连接服务器失败");
            }

            @Override
            public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
                super.onSocketWriteResponse(info, action, data);
                Log.i(TAG, "客户端发送数据回调");
            }

            @Override
            public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
                super.onPulseSend(info, data);
                Log.i(TAG, "客户端发送心跳包");
            }
        });
        //调用通道进行连接
        manager.connect();
    }

    public static void unconnect() {
        manager.disconnect();
    }
}
