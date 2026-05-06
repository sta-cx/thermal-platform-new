package org.sdkj.thermal.wechat.utils;

import org.sdkj.common.core.exception.ServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.sdkj.thermal.wechat.entity.rest.OauthInfo;
import org.sdkj.thermal.wechat.entity.rest.WeixinUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class WeixinAPIHelper {

    protected static Logger log = LoggerFactory.getLogger(WeixinAPIHelper.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    //SD
    public static String APPID = "wx4891a006517c8816";
    public static String SECRET = "0be23fed653d219cf0419ea82009593c";

    public static String auth_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope=snsapi_base&state=1#wechat_redirect";

    private static String auth_openid_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code";

    private static String getGroupByOpenId_url = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token={0}";

    private static String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";

    private static String getUserInfoUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}";

    private static String getUserInfoUrl2 = "https://api.weixin.qq.com/cgi-bin/user/get?access_token={0}&openid={1}";

    private static String sendMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/send?access_token={0}&scope=snsapi_base";

    private static String sendTemplateMsgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={0}";

    private static String createMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token={0}";

    private static String deleteMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token={0}";

    public static OauthInfo getOauthInfo(String appid, String secret, String code) {
        OauthInfo oauthInfo = null;
        try {
            log.info("getAccessToken start.appid={},secret={}", appid, secret);
            String url = MessageFormat.format(auth_openid_url, appid, secret, code);
            String response = HttpUtil.doGet(url);
            JsonNode jsonObject = mapper.readTree(response);
            oauthInfo = new OauthInfo();
            oauthInfo.setAccessToken(jsonObject.path("access_token").asText());
            oauthInfo.setExpiresIn(jsonObject.path("expires_in").asInt());
            oauthInfo.setRefreshToken(jsonObject.path("refresh_token").asText());
            oauthInfo.setOpenId(jsonObject.path("openid").asText());
            oauthInfo.setScope(jsonObject.path("scope").asText());
        } catch (IOException e) {
            log.error("get access token failed: network error", e);
        } catch (NullPointerException e) {
            log.error("get access token failed: unexpected response structure", e);
        }
        return oauthInfo;
    }

    public static String getAccessToken(String appid, String secret) {
        String accessToken = null;
        try {
            log.info("getAccessToken start.appid={},secret={}", appid, secret);
            String url = MessageFormat.format(getTokenUrl, appid, secret);
            String response = HttpUtil.doGet(url);
            accessToken = mapper.readTree(response).path("access_token").asText();
        } catch (IOException e) {
            log.error("get access token failed: network error", e);
        }
        return accessToken;
    }

    public static String sendMessage(String token, String msg) {
        try {
            log.info("sendMessage start.token:{},msg:{}", token, msg);
            String url = MessageFormat.format(sendMsgUrl, token);
            return HttpUtil.doPost(url, msg);
        } catch (Exception e) {
            log.error("send message failed", e);
            return null;
        }
    }

    public static String sendTemplateMessage(String token, String msg) {
        try {
            log.info("sendTemplateMessage start.token:{},msg:{}", token, msg);
            String url = MessageFormat.format(sendTemplateMsgUrl, token);
            return HttpUtil.doPost(url, msg);
        } catch (Exception e) {
            log.error("send template message failed", e);
            return null;
        }
    }

    public static String createMenu(String token, String menu) {
        try {
            log.info("createMenu start.token:{},menu:{}", token, menu);
            String url = MessageFormat.format(createMenuUrl, token);
            return HttpUtil.doPost(url, menu);
        } catch (Exception e) {
            log.error("create menu failed", e);
            return null;
        }
    }

    public static String deleteMenu(String token) {
        try {
            log.info("deleteMenu start.token:{}", token);
            String url = MessageFormat.format(deleteMenuUrl, token);
            return HttpUtil.doGet(url);
        } catch (Exception e) {
            log.error("delete menu failed", e);
            return null;
        }
    }

    public static WeixinUser getUserInfo(String token, String openid) {
        WeixinUser weixinUserInfo = null;
        JsonNode jsonObject = null;
        try {
            log.info("getUserInfo start.token:{},openid:{}", token, openid);
            String url = MessageFormat.format(getUserInfoUrl, token, openid);
            String response = HttpUtil.doGet(url);
            jsonObject = mapper.readTree(response);
            if (jsonObject != null && jsonObject.has("openid")) {
                weixinUserInfo = new WeixinUser();
                weixinUserInfo.setOpenId(jsonObject.path("openid").asText());
                weixinUserInfo.setSubscribe(jsonObject.path("subscribe").asInt());
                weixinUserInfo.setSubscribeTime(DateUtil.timeStamp2Date(
                        String.valueOf(jsonObject.path("subscribe_time").asInt()), "yyyy-MM-dd HH:mm:ss"));
                weixinUserInfo.setNickname(jsonObject.path("nickname").asText());
                weixinUserInfo.setSex(jsonObject.path("sex").asInt());
                weixinUserInfo.setCountry(jsonObject.path("country").asText());
                weixinUserInfo.setProvince(jsonObject.path("province").asText());
                weixinUserInfo.setCity(jsonObject.path("city").asText());
                weixinUserInfo.setLanguage(jsonObject.path("language").asText());
                weixinUserInfo.setHeadImgUrl(jsonObject.path("headimgurl").asText());
                weixinUserInfo.setGroupId((long) jsonObject.path("groupid").asInt());
            }
        } catch (IOException e) {
            log.error("get user info failed: network error", e);
        } catch (NullPointerException e) {
            log.error("get user info failed: unexpected response structure");
            if (weixinUserInfo != null && weixinUserInfo.getSubscribe() == 0) {
                log.error("用户{}已取消关注", weixinUserInfo.getOpenId());
            } else if (jsonObject != null) {
                int errorCode = jsonObject.path("errcode").asInt();
                String errorMsg = jsonObject.path("errmsg").asText();
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return weixinUserInfo;
    }

    public static String getCode() {
        String code = "";
        try {
            String REDIRECT_URI = URLDecoder.decode(GlobalConstants.WEIXIN_DOMAIN, "UTF-8");
            String url = MessageFormat.format(auth_url, APPID, REDIRECT_URI);
            url = URLDecoder.decode(url);
            String jsonStr = HttpUtil.doGet(url);
            jsonStr = new String(jsonStr.getBytes(), "UTF-8");
            JsonNode root = mapper.readTree(jsonStr);
            if (root.has("code")) {
                code = root.get("code").asText();
            }
        } catch (IOException e) {
            log.error("获取微信授权码失败: network error", e);
        }
        return code;
    }

    public static String getopendid(String code) {
        String openid = "";
        try {
            log.info("getUserInfo start.code:{}", code);
            String url = MessageFormat.format(auth_openid_url, APPID, SECRET, code);
            String jsonStr = HttpUtil.doGet(url);
            JsonNode root = mapper.readTree(jsonStr);
            if (root.has("openid")) {
                openid = root.get("openid").asText();
            }
        } catch (IOException e) {
            log.error("获取openid失败: network error", e);
        }
        return openid;
    }

    public static List<String> getUserInfo2(String token, String openid) {
        JsonNode jsonObject = null;
        List<String> openids = new ArrayList<>();
        try {
            log.info("getUserInfo start.token:{},openid:{}", token, openid);
            String url = MessageFormat.format(getUserInfoUrl2, token, openid);
            String response = HttpUtil.doGet(url);
            jsonObject = mapper.readTree(response);
            if (jsonObject != null && jsonObject.has("data")) {
                JsonNode data = jsonObject.get("data");
                if (data.has("openid")) {
                    ArrayNode openidArray = (ArrayNode) data.get("openid");
                    for (int i = 0; i < openidArray.size(); i++) {
                        openids.add(openidArray.get(i).asText());
                    }
                }
            }
        } catch (IOException e) {
            log.error("获取用户列表失败: network error", e);
        }
        return openids;
    }

    public static String getUserGroup(String accessToken, String openId) {
        String str = "";
        try {
            String url = MessageFormat.format(getGroupByOpenId_url, accessToken);
            ObjectNode j = mapper.createObjectNode();
            j.put("openid", openId);
            String jsonStr = HttpUtil.doPost(url, j.toString());
            JsonNode root = mapper.readTree(jsonStr);
            if (root.has("groupid")) {
                str = root.get("groupid").asText();
            }
        } catch (IOException e) {
            log.error("获取用户分组失败: network error", e);
        }
        return str;
    }

    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("操作异常", e);
        }
        return result;
    }

    public static String rsaEncryptOAEP(String message, PublicKey publicKey)
            throws IllegalBlockSizeException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] data = message.getBytes("utf-8");
            byte[] cipherdata = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(cipherdata);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new ServiceException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的公钥", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
        }
    }

    public static String rsaEncryptOAEP(String message, X509Certificate certificate)
            throws IllegalBlockSizeException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, certificate.getPublicKey());
            byte[] data = message.getBytes("utf-8");
            byte[] cipherdata = cipher.doFinal(data);
            return Base64.getEncoder().encodeToString(cipherdata);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new ServiceException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的证书", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
        }
    }

    public static String rsaDecryptOAEP(String ciphertext, PrivateKey privateKey)
            throws BadPaddingException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] data = Base64.getDecoder().decode(ciphertext);
            return new String(cipher.doFinal(data), "utf-8");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new ServiceException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的私钥", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new BadPaddingException("解密失败");
        }
    }
}
