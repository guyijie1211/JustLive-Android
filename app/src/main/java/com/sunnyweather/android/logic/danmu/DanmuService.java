package com.sunnyweather.android.logic.danmu;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSONObject;
import com.sunnyweather.android.ui.liveRoom.LiveRoomViewModel;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class DanmuService {
    String platform;
    String roomId;
    WebSocket webSocket;
    OkHttpClient mClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间]
            .build();

    String douyuUrl = "wss://danmuproxy.douyu.com:8503/";
    String bilibiliUrl = "wss://broadcastlv.chat.bilibili.com:2245/sub";
    Request request = new Request.Builder().get().url(douyuUrl).build();
    Timer myTimer = new Timer();

//    AddRoomData addRoomData = new AddRoomData();
//        addRoomData.setRoomId(6136246);
//    String data = JSON.toJSONString(addRoomData);
//    int dataLen = data.length() + 16;
//    byte[] openMessage = byteMergerAll(intToByteBig(dataLen), shortToByteBig((short)16), shortToByteBig((short)1),
//            intToByteBig(7), intToByteBig(1), data.getBytes(StandardCharsets.UTF_8));
//    byte[] heartMessage = byteMergerAll(intToByteBig(16), shortToByteBig((short)16), shortToByteBig((short)1),
//            intToByteBig(2), intToByteBig(1));
//    ByteString byteString = new ByteString(openMessage);
//    ByteString byteStringHeart = new ByteString(heartMessage);
//    //开始连接
//    private String platform;
//    private String roomId;

    public DanmuService(String platform, String roomId) {
        this.platform = platform;
        this.roomId = roomId;
    }

    //连接弹幕服务器
    public void connect(ArrayList<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum) {
        //开始连接
        webSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                sendOpenMsgDouyu(webSocket, roomId);
                System.out.println("onOpen");
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                JSONObject jsonObject = null;
                try {
                    jsonObject = douyuDecode(bytes);
                    if (jsonObject.getString("type").equals("chatmsg")) {
                        resultList.add(new LiveRoomViewModel.DanmuInfo(jsonObject.getString("nn"), jsonObject.getString("txt")));
                        danmuNum.postValue(0);
                        System.out.println(jsonObject.getString("nn") + "：" + jsonObject.getString("txt"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                //连接关闭...
                System.out.println("onClosed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                //连接失败...
                System.out.println("onFailure");
            }
        });
    }

    //斗鱼发送入场消息
    private void sendOpenMsgDouyu(WebSocket webSocket, String roomId) {
        String loginMsg = "type@=loginreq/roomid@=" + roomId + "/";
        String joinGroupMsg ="type@=joingroup/rid@=" + roomId + "/gid@=1/";
        String heartMsg ="type@=mrkl/";

        ByteString heart = douyuEncode(heartMsg);
        webSocket.send(douyuEncode(loginMsg));
        webSocket.send(douyuEncode(joinGroupMsg));
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("=============================task======================");
                webSocket.send(heart);
            }
        }, 1000, 45000);
    }

    //斗鱼编码
    private ByteString douyuEncode(String msg) {
        int loginLen = msg.length() + 9;
        msg = msg + "\0";
        byte[] loginMessage = byteMergerAll(intToByteLittle(loginLen), intToByteLittle(loginLen), shortToByteLittle((short)689),
                shortToByteLittle((short)0), msg.getBytes(StandardCharsets.UTF_8));
        return ByteString.of(loginMessage);
    }

    //斗鱼解码   读取顺序不对
    private JSONObject douyuDecode(ByteString byteString) throws IOException {
        byte[] data = byteString.toByteArray();
        int msgLength = bytes2IntLittle(data, 0);
        int op = bytes2IntLittle(data, 8);
        if(op == 690){
            byte[] msgBody = subByte(data, 12, msgLength - 10);
            String jsonStr = new String(msgBody, StandardCharsets.UTF_8);
            jsonStr = jsonStr.replaceAll("@=", "\":\"").replaceAll("/", "\",\"");
            jsonStr = jsonStr.replaceAll("@A", "@").replaceAll("@S", "/");
            String body = "{\"" + jsonStr + "\"}";
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        }
        return null;
    }

    public void stop() {
        webSocket.cancel();
        myTimer.cancel();
    }

    //处理接收消息
    private String handleMessage(byte[] data) throws IOException, DataFormatException {
        String result = "";
        int dataLength = data.length;
        if (dataLength < 16) {
            System.out.println("数据错误");
        }
        else {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
            int msgLength = inputStream.readInt();
            System.out.println(msgLength);
            if (msgLength < 16) {
                System.out.println("maybe need expand size of cache");
            } else if (msgLength > 16 && msgLength == dataLength) {

                short headerLength = inputStream.readShort();
                short version = inputStream.readShort();

                int action = inputStream.readInt() - 1;
                // 直播间在线用户数目
                if (action == 2) {
                    System.out.println("用户数目消息");
                } else if (action == 4) {
                    int param = inputStream.readInt();
                    int msgBodyLength = dataLength - 16;
                    byte[] msgBody = new byte[msgBodyLength];
                    inputStream.read(msgBody, 0, msgBodyLength);
                    if (version != 2) {
                        String jsonStr = new String(msgBody, StandardCharsets.UTF_8);
                        return jsonStr;
                    } else if (action == 4){
                        Inflater inflater = new Inflater();
                        inflater.setInput(msgBody);
                        while (!inflater.finished()) {
                            byte[] header = new byte[16];
                            inflater.inflate(header, 0, 16);
                            DataInputStream headerStream  = new DataInputStream(new ByteArrayInputStream(header));
                            int innerMsgLen = headerStream.readInt();
                            short innerHeaderLength = headerStream.readShort();
                            short innerVersion = headerStream.readShort();
                            int innerAction = headerStream.readInt() - 1;
                            int innerParam = headerStream.readInt();
                            byte[] innerData = new byte[innerMsgLen - 16];
                            inflater.inflate(innerData, 0, innerData.length);
                            if (innerAction == 4) {
//                                System.out.println("======================\npppppppppppppppppppppppppp\nppppppppppppppppppppppp\nppppppppppppppppppppppppppppppppppp");
                                String jsonStr = new String(innerData, StandardCharsets.UTF_8);
                                return jsonStr;
                            } else if (innerAction == 2) {
                                // pass
                            }
                        }
                    }
                } else if (msgLength > 16 && msgLength < dataLength) {
                    byte[] singleData = new byte[msgLength];
                    System.arraycopy(data, 0, singleData, 0, msgLength);
                    handleMessage(singleData);
                    int remainLen = dataLength - msgLength;
                    byte[] remainDate = new byte[remainLen];
                    System.arraycopy(data, msgLength, remainDate, 0, remainLen);
                    handleMessage(remainDate);
                }
            }
        }
        return result;
    }

    /**
     * 截取byte数组   不改变原数组
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    private byte[] subByte(byte[] b,int off,int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    //int 转 byte[]   低字节在前（小端整数,斗鱼）
    private byte[] intToByteLittle(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    //byte数组到int的转换(小端)
    private int bytes2IntLittle(byte[] bytes, int startIndex) {
        int int1=bytes[0 + startIndex]&0xff;
        int int2=(bytes[1 + startIndex]&0xff)<<8;
        int int3=(bytes[2 + startIndex]&0xff)<<16;
        int int4=(bytes[3 + startIndex]&0xff)<<24;

        return int1|int2|int3|int4;
    }

    //byte数组到int的转换(大端)
    private int bytes2IntBig(byte[] bytes ) {
        int int1=bytes[3]&0xff;
        int int2=(bytes[2]&0xff)<<8;
        int int3=(bytes[1]&0xff)<<16;
        int int4=(bytes[0]&0xff)<<24;

        return int1|int2|int3|int4;
    }

    //读取小端byte数组为short
    private short byteToShortLittle(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    //读取大端byte数组为short
    private short byteToShortBig(byte[] b) {
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }

    //int 转 byte[]   高字节在前（大端整数,Bili）
    private byte[] intToByteBig(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    //将short转为高字节在前，低字节在后的byte数组（大端）
    private byte[] shortToByteBig(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    //将short转为低字节在前，高字节在后的byte数组(小端)
    private byte[] shortToByteLittle(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    //合并多个byte数组
    private byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }
}
