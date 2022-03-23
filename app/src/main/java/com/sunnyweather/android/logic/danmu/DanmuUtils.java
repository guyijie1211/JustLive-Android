package com.sunnyweather.android.logic.danmu;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunnyweather.android.ui.liveRoom.LiveRoomViewModel;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

public class DanmuUtils {
    public static byte[] nullBytes = new byte[16];

    static String douyuUrl = "wss://danmuproxy.douyu.com:8503/";
    static String bilibiliUrl = "wss://broadcastlv.chat.bilibili.com:2245/sub";
    static List<String> isActiveArray = new ArrayList<>();
    static Boolean isActive = false;
    //获取请求对象
    public static Request getRequest(String platform, String roomId) {
        Request request = null;
        if (platform.equals("douyu")) {
            request = new Request.Builder().get().url(douyuUrl).build();
        }
        if (platform.equals("huya")) {
            request = new Request.Builder().get().url(DanmuUtils.getHuyaUri(Long.valueOf(roomId))).build();
        }
        if (platform.equals("bilibili")) {
            request = new Request.Builder().get().url(bilibiliUrl).build();
        }
        return request;
    }
    //发送接入请求
    public static void sendOpenMsg(WebSocket webSocket, String platform, String roomId, Timer myTimer) {
        if (platform.equals("douyu")) {
            sendOpenMsgDouyu(webSocket, roomId, myTimer);
        }
        if (platform.equals("huya")) {
            sendOpenMsgHuya(webSocket);
        }
        if (platform.equals("bilibili")) {
            sendOpenMsgBilibili(webSocket, roomId, myTimer);
        }
    }
    //处理字节消息
    public static void onMessage(String platform, ByteString bytes, List<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum, List<String> activeArray, Boolean isActived) {
        isActiveArray = activeArray;
        isActive = isActived;
        if (platform.equals("douyu")) {
            onMessageDouyu(bytes, resultList, danmuNum);
        }
        if (platform.equals("bilibili")) {
            try {
                onMessageBilibili(bytes, resultList, danmuNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //处理字符串消息
    public static void onMessageString(String platform, String bytes, List<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum, List<String> activeArray, Boolean isActived) {
        isActiveArray = activeArray;
        isActive = isActived;
        if (platform.equals("huya")) {
            omMessageHuya(bytes, resultList, danmuNum);
        }
    }

    //=============================虎牙==================================

    //获取虎牙链接地址
    public static String getHuyaUri(Long roomId){
        String appId = "i210201fd32df6f2";
        String secret = "4ea4bd3e81acc55f1852d252c0cb5972";
        Map<String, Object> map = getWebSocketJwtParamsMap(appId,secret,roomId);

        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append("ws://ws-apiext.huya.com/index.html").append(MapToUrlString(map));
        return urlBuffer.toString();
    }
    //虎牙处理收到消息
    public static void omMessageHuya(String text, List<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum) {
        try {
            JSONObject res = JSONObject.parseObject(text);
            if("getMessageNotice".equals(res.getString("notice"))) {
                JSONObject data = JSONObject.parseObject(text).getJSONObject("data");
                //弹幕内容
                String content = data.getString("content");
                //弹幕类型，0-常规弹幕，1-功能性弹幕(诸如部分投票/战队支持等相关弹幕)，2-上电视弹幕
                Integer msgType = data.getInteger("msgType");
                //发送弹幕的用户昵称
                String sendNick = data.getString("sendNick");
                //用户等级
                Long senderLevel = data.getLong("senderLevel");
                if (msgType != 2 && (!isActive || !isBanned(content))) {
                    resultList.add(new LiveRoomViewModel.DanmuInfo(sendNick, content));
                    danmuNum.postValue(0);
                }
            }
        } catch (Exception e) {
            System.out.println("-------- 数据处理异常 --------");
        }
    }
    /**
     * 生成开放API Websocket连接参数
     * @param appId  开发者ID（https://ext.huya.com成为开发者后自动生成）
     * @param secret 开发者密钥（https://ext.huya.com成为开发者后自动生成）
     * @param roomId 要监听主播的房间号
     * @return
     */
    public static Map<String, Object> getWebSocketJwtParamsMap(String appId, String secret, long roomId){
        //获取时间戳（毫秒）
        long currentTimeMillis = System.currentTimeMillis();
        long expireTimeMillis = System.currentTimeMillis() + 10 * 60 * 1000;  //超时时间:通常设置10分钟有效，即exp=iat+600，注意不少于当前时间且不超过当前时间60分钟
        Date iat = new Date(currentTimeMillis);
        Date exp = new Date(expireTimeMillis);

        try {

            Map<String, Object> header = new HashMap<String, Object>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            //生成JWT凭证
            String sToken = Jwts.builder()
                    .setHeader(header)
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .claim("appId", appId)
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            Map<String, Object> authMap = new HashMap<String, Object>();
            authMap.put("iat", currentTimeMillis / 1000);    //jwt凭证生成时间戳（秒）
            authMap.put("exp", expireTimeMillis / 1000);     //jwt凭证超时时间戳（秒）
            authMap.put("sToken", sToken);                   //jwt签名串
            authMap.put("appId",appId);                      //开发者ID
            authMap.put("do", "comm");                       //接口默认参数
            authMap.put("roomId", roomId);                   //需要监听主播的房间号

            return authMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String MapToUrlString(Map<String,Object> paramsMap){
        if (null == paramsMap)
            return null;
        String preStr = "?";
        for (Map.Entry<String,Object> entry:paramsMap.entrySet()){
            try {
                preStr = preStr + entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(),"UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return preStr.substring(0,preStr.length() - 1);
    }
    //发送虎牙链接请求
    public static void sendOpenMsgHuya(WebSocket webSocket) {
        Long reqId = System.currentTimeMillis();
        String sendMsg = "{\"command\":\"subscribeNotice\",\"data\":[\"getMessageNotice\"],\"reqId\":\"" + reqId + "\"}";
        webSocket.send(sendMsg);
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                webSocket.send("ping");
            }
        }, 0, 15000);
    }

    //=============================斗鱼==================================

    //斗鱼发送入场消息
    public static void sendOpenMsgDouyu(WebSocket webSocket, String roomId, Timer myTimer) {
        String loginMsg = "type@=loginreq/roomid@=" + roomId + "/";
        String joinGroupMsg ="type@=joingroup/rid@=" + roomId + "/gid@=1/";
        String heartMsg ="type@=mrkl/";

        ByteString heart = douyuEncode(heartMsg);
        webSocket.send(douyuEncode(loginMsg));
        webSocket.send(douyuEncode(joinGroupMsg));
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                webSocket.send(heart);
            }
        }, 1000, 45000);
    }
    //斗鱼处理收到消息
    private static void onMessageDouyu(ByteString bytes, List<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum) {
        JSONObject jsonObject;
        try {
            jsonObject = DanmuUtils.douyuDecode(bytes);
            String name = jsonObject.getString("nn");
            String content = jsonObject.getString("txt");
            if (jsonObject.getString("type").equals("chatmsg") && (!isActive || !isBanned(content))) {
                resultList.add(new LiveRoomViewModel.DanmuInfo(name, content));
                danmuNum.postValue(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //斗鱼编码
    public static ByteString douyuEncode(String msg) {
        int loginLen = msg.length() + 9;
        msg = msg + "\0";
        byte[] loginMessage = byteMergerAll(intToByteLittle(loginLen), intToByteLittle(loginLen), shortToByteLittle((short)689),
                shortToByteLittle((short)0), msg.getBytes(StandardCharsets.UTF_8));
        return ByteString.of(loginMessage);
    }
    //斗鱼解码
    public static JSONObject douyuDecode(ByteString byteString) throws IOException {
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
    /**
     * 截取byte数组   不改变原数组
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b,int off,int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }
    //int 转 byte[]   低字节在前（小端整数,斗鱼）
    public static byte[] intToByteLittle(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
    //byte数组到int的转换(小端)
    public static int bytes2IntLittle(byte[] bytes, int startIndex) {
        int int1=bytes[0 + startIndex]&0xff;
        int int2=(bytes[1 + startIndex]&0xff)<<8;
        int int3=(bytes[2 + startIndex]&0xff)<<16;
        int int4=(bytes[3 + startIndex]&0xff)<<24;

        return int1|int2|int3|int4;
    }
    //将short转为低字节在前，高字节在后的byte数组(小端)
    public static byte[] shortToByteLittle(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }
    //合并多个byte数组
    public static byte[] byteMergerAll(byte[]... values) {
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

    //=====================================bilibili=================================

    //bilibili发送入场消息
    public static void sendOpenMsgBilibili(WebSocket webSocket, String roomId, Timer myTimer) {
        AddRoomData addRoomData = new AddRoomData();
        addRoomData.setRoomId(Long.valueOf(roomId));
        String data = "{\"roomid\":" + roomId + "}";
        int dataLen = data.length() + 16;
        byte[] openMessage = byteMergerAll(intToByteBig(dataLen), shortToByteBig((short)16), shortToByteBig((short)1),
                intToByteBig(7), intToByteBig(1), data.getBytes(StandardCharsets.UTF_8));
        byte[] heartMessage = byteMergerAll(intToByteBig(16), shortToByteBig((short)16), shortToByteBig((short)1),
                intToByteBig(2), intToByteBig(1));
        ByteString byteString = ByteString.of(openMessage);
        ByteString byteStringHeart = ByteString.of(heartMessage);
        webSocket.send(byteString);
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                webSocket.send(byteStringHeart);
            }
        }, 10, 30000);
    }
    //bilibili处理收到消息
    private static void onMessageBilibili(ByteString bytes, List<LiveRoomViewModel.DanmuInfo> resultList, MutableLiveData<Integer> danmuNum) throws IOException, DataFormatException {
        byte[] data = bytes.toByteArray();
        int dataLength = data.length;
        if (dataLength < 16) {
            System.out.println("数据错误");
        }
        else {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
            int msgLength = inputStream.readInt();
            if (msgLength < 16) {
                System.out.println("maybe need expand size of cache");
            } else if (msgLength > 16 && msgLength == dataLength) {
                short headerLength = inputStream.readShort();
                short version = inputStream.readShort();
                int action = inputStream.readInt() - 1;
                // 直播间在线用户数目
                if (action == 2) {
                } else if (action == 4) {
                    int param = inputStream.readInt();
                    int msgBodyLength = dataLength - 16;
                    byte[] msgBody = new byte[msgBodyLength];
                    inputStream.read(msgBody, 0, msgBodyLength);
                    if (version != 2) {
                        String jsonStr = new String(msgBody, StandardCharsets.UTF_8);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (jsonObject != null) {
                            String msgType = jsonObject.getString("cmd");
                            if ("DANMU_MSG".equals(msgType)) {
                                JSONArray obj = jsonObject.getJSONArray("info");
                                String userName = obj.getJSONArray(2).getString(1);
                                String danmu = obj.getString(1);
                                if (!isActive || !isBanned(danmu)) {
                                    resultList.add(new LiveRoomViewModel.DanmuInfo(userName, danmu));
                                    danmuNum.postValue(0);
                                }
                            }
                        }
                    } else if (action == 4){
                        Inflater inflater = new Inflater();
                        inflater.setInput(msgBody);
                        while (!inflater.finished()) {
                            byte[] header = new byte[16];
                            inflater.inflate(header, 0, 16);
                            while (!header.equals(nullBytes)) {
                                DataInputStream headerStream  = new DataInputStream(new ByteArrayInputStream(header));
                                int innerMsgLen = headerStream.readInt();
                                headerStream.readShort();
                                headerStream.readShort();
                                int innerAction = headerStream.readInt() - 1;
                                headerStream.readInt();
                                byte[] innerData = new byte[innerMsgLen - 16];
                                inflater.inflate(innerData, 0, innerData.length);
                                if (innerAction == 4) {
                                    String jsonStr = new String(innerData, StandardCharsets.UTF_8);
                                    if (jsonStr.equals(new String(new byte[innerMsgLen - 16], 0, innerMsgLen - 16, StandardCharsets.UTF_8))) break;
                                    JSONObject jsonObject = JSON.parseObject(jsonStr);
                                    if (jsonObject != null) {
                                        String msgType = jsonObject.getString("cmd");
                                        if ("DANMU_MSG".equals(msgType)) {
                                            JSONArray obj = jsonObject.getJSONArray("info");
                                            String userName = obj.getJSONArray(2).getString(1);
                                            String danmu = obj.getString(1);
                                            if (!isActive || !isBanned(danmu)) {
                                                resultList.add(new LiveRoomViewModel.DanmuInfo(userName, danmu));
                                                danmuNum.postValue(0);
                                            }
                                        }
                                    }
                                }
                                inflater.inflate(header, 0, 16);
                            }
                        }
                    }else if (msgLength > 16 && msgLength < dataLength) {
                        byte[] singleData = new byte[msgLength];
                        System.arraycopy(data, 0, singleData, 0, msgLength);
                        onMessageBilibili (ByteString.of(singleData), resultList, danmuNum);
                        int remainLen = dataLength - msgLength;
                        byte[] remainDate = new byte[remainLen];
                        System.arraycopy(data, msgLength, remainDate, 0, remainLen);
                        onMessageBilibili (ByteString.of(remainDate), resultList, danmuNum);
                    }
                }
            }
        }
    }
    //int 转 byte[]   高字节在前（大端整数,Bili）
    public static byte[] intToByteBig(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }
    //将short转为高字节在前，低字节在后的byte数组（大端）
    public static byte[] shortToByteBig(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }
    static class AddRoomData{
        @JSONField(name="roomid")
        private long roomId;

        public void setRoomId(long roomId) {
            this.roomId = roomId;
        }
    }

    //=================================屏蔽判断==================================

    /**
     * 判断弹幕是否被屏蔽
     * @param content 弹幕内容
     * @return
     */
    private static Boolean isBanned(String content) {
        for (String ban : isActiveArray) {
            if (isBannedSingle(content, ban)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断单条屏蔽规则是否生效
     * @param content 弹幕内容
     * @param banRule 屏蔽规则
     * @return
     */
    private static Boolean isBannedSingle(String content, String banRule) {
        if (banRule.startsWith("/") && banRule.endsWith("/")) {
            banRule = banRule.substring(1, banRule.length() - 1);
            Pattern p = Pattern.compile(banRule);
            Matcher m = p.matcher(content);
            if (m.matches()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (content.contains(banRule)) {
                return true;
            } else {
                return false;
            }
        }
    }
}

