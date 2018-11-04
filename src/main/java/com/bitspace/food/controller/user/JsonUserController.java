package com.bitspace.food.controller.user;


import com.bitspace.food.base.*;
import com.bitspace.food.config.SystemConfig;
import com.bitspace.food.constants.Pager;
import com.bitspace.food.controller.AbstractController;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.entity.*;
import com.bitspace.food.memdb.*;
import com.bitspace.food.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.asynchttpclient.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

@Controller
public class JsonUserController extends AbstractController {
    /**
     * 注册
     *
     * @param request
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/register", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> register(HttpServletRequest request,
                                           @RequestParam(value = "countryCode", required = false) String countryCode,
                                           @RequestParam(value = "phone", required = true) String phone,
                                           @RequestParam(value = "password", required = true) String password,
                                           @RequestParam(value = "imgCode", required = true) String imgCode,
                                           @RequestParam(value = "source", required = true) Byte source) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        imgCode = StringUtils.trimToNull(imgCode);
        phone = StringUtils.trimToNull(phone);
        password = StringUtils.trimToNull(password);
        if(countryCode == null) countryCode = "86";
        //判断手机号是否为空
        if (null == phone) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ERROR));
            return dr;
        }
        //密码验证
        if (password == null || password.length() < 4) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PWD_ERROR));
            return dr;
        }
        if(imgCode == null || imgCode.length() > 4){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_INVALID));
            return dr;
        }
        User user = new User();
        user.setUserName(phone);
        user.setUserImg(User.U_IMG);
        user.setMobile(phone);
        user.setLoginPwd(password);
        user.setPayPwd(User.PAY_PWD);
        user.setStatus(User.S_VERIFING);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        user.setSource(source);
        user.setRole(User.R_USER);
        user.setCountryCode(countryCode);
        //验证手机号是否存在
        if (UserMemDB.getUserByPhone(user.getMobile()) != null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_EXISTS));
            return dr;
        }
        Dispatcher dispatcher = Dispatcher.getInstance();
        //录入
        dispatcher.controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_CHECK,
                new Tuple<>(FormatUtil.CountryPhone(phone), imgCode))
                .thenCompose(e -> {
                    return dispatcher.controllerAppSendMsg(ProcessorType.USER, EventType.USER_REGISTER, user);
                }).thenAccept(e -> {
            try {
                String session = JwtUtil.createJwtToken(user.getUid());
                SessionMemDB.putSession(user.getUid(), session);
                Map<String, Object> result = new HashMap<>();
                result.put("systemLoginKey", session);
                result.put("uid", user.getUid());
                result.put("userName", user.getUserName());
                result.put("mobile", user.getMobile());
                result.put("userImg",user.getUserImg());
                dr.setResult(JsonResultWrapper.succStr(result));
            } catch (Exception exception) {
                LoggerUtil.error(log, exception);
                //此处提示信息应改为 注册失败
                dr.setResult(JsonResult.errResultString(ErrMessage.CODE_SYS_UNKNOWN));
            }
        }) .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 登录
     *
     * @param request
     * @param phone
     * @param password
     * @param imgCode
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> login(HttpServletRequest request,
                                        @RequestParam(value = "phone", required = true) String phone,
                                        @RequestParam(value = "password", required = true) String password,
                                        @RequestParam(value = "imgCode", required = true) String imgCode) {
        DeferredResult<String> dr = new DeferredResult<>();
        phone = StringUtils.trimToNull(phone);
        User user = UserMemDB.getUserByPhone(phone);
        if (phone == null || user == null || !user.isUser()) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        //验证密码
        if (!Objects.equals(password, user.getLoginPwd())) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_PASSWD_INCORRECT));
            return dr;
        }
        if(imgCode == null || imgCode.length() > 4){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_IMAGE_CODE_INVALID));
            return dr;
        }
        //校验图片验证码
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_CHECK,
                new Tuple<>(FormatUtil.CountryPhone(phone), imgCode))
                .thenAccept(e -> {
                    try {
                        String session = JwtUtil.createJwtToken(user.getUid());
                        SessionMemDB.putSession(user.getUid(), session);
                        Map<String, Object> result = new HashMap<>();
                        result.put("systemLoginKey", session);
                        result.put("uid", user.getUid());
                        result.put("userName", user.getUserName());
                        result.put("mobile", user.getMobile());
                        dr.setResult(JsonResultWrapper.succStr(result));
                    } catch (Exception exception) {
                        LoggerUtil.error(log, exception);
                        dr.setResult(JsonResult.errResultString(ErrMessage.CODE_SYS_UNKNOWN));
                    }
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 获取图片验证码
     *
     * @param request
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/imgCode", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getCaptcha(HttpServletRequest request,
                                             @RequestParam(value = "loginName", required = false) String loginName) {

        DeferredResult<String> dr = new DeferredResult<>();
    
        loginName = StringUtils.trimToNull(loginName);

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_IMGCODE_GENERATE,
                FormatUtil.CountryPhone(loginName))
                .thenAccept(e -> {

                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 获取短信验证码
     *
     * @param request
     * @param phone
     * @param type
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/captcha", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getCaptcha(HttpServletRequest request,
                                             @RequestParam(value = "phone", required = true) String phone,
                                             @RequestParam(value = "type", required = true) Byte type) {

        DeferredResult<String> dr = new DeferredResult<>();
        //手机号 验证类型
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_GENERATE, Pair.of(type, FormatUtil.CountryPhone(phone)))
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    LoggerUtil.debug(log, "captcha:" + rsp.getData());
                    dr.setResult(JsonResult.SUCCESS_RESULT_JSON);
                })
                .fail(e -> {
                    System.out.println(e);
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 忘记密码(修改密码)
     *
     * @param request
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/updatePwd", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> updatePwd(HttpServletRequest request,
                                            @RequestParam(value = "phone", required = true) String phone,
                                            @RequestParam(value = "password", required = true) String password,
                                            @RequestParam(value = "captcha", required = true) String captcha) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        captcha = StringUtils.trimToNull(captcha);
        phone = StringUtils.trimToNull(phone);
        password = StringUtils.trimToNull(password);
        //判断手机号是否为空
        if (null == phone) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ERROR));
            return dr;
        }
        //校验短信验证码格式
        if (captcha == null || captcha.length() > 6) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_INVALID));
            return dr;
        }
        //密码验证
        if (password == null || password.length() < 4) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PWD_ERROR));
            return dr;
        }
        User user = UserMemDB.getUserByPhone(phone);
        //验证用户是否存在
        if (user == null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        user.setLoginPwd(password);
        user.setUpdateTime(System.currentTimeMillis());
        Dispatcher dispatcher = Dispatcher.getInstance();
        //录入
        dispatcher.controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_CHECK,
                new TupleOf3<>(CaptchaMemDB.TP_FORGET_PWD_LOGIN, phone, captcha))
                .thenCompose(e -> {
                    return dispatcher.controllerAppSendMsg(ProcessorType.USER, EventType.UPDATE_PWD, user);
                })
                .thenAccept(e -> {
                    dr.setResult(JsonResult.SUCCESS_RESULT_JSON);
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 重置交易密码
     *
     * @param request
     * @param phone
     * @param password
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/updatePricePwd", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> updatePricePwd(HttpServletRequest request,
                                                 @RequestParam(value = "phone", required = true) String phone,
                                                 @RequestParam(value = "password", required = true) String password,
                                                 @RequestParam(value = "captcha", required = true) String captcha) {
        // 手机号 密码 短信验证码
        DeferredResult<String> dr = new DeferredResult<>();
        captcha = StringUtils.trimToNull(captcha);
        phone = StringUtils.trimToNull(phone);
        password = StringUtils.trimToNull(password);
        //判断手机号是否为空
        if (null == phone) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_ERROR));
            return dr;
        }
        //校验短信验证码格式
        if (captcha == null || captcha.length() > 6) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_INVALID));
            return dr;
        }
        //密码验证
        if (password == null || password.length() < 4) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PWD_ERROR));
            return dr;
        }
        User user = UserMemDB.getUserByPhone(phone);
        //验证用户是否存在
        if (user == null) {
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_USER_NOT_EXISTS));
            return dr;
        }
        user.setPricePassword(password);
        user.setUpdateTime(System.currentTimeMillis());
        Dispatcher dispatcher = Dispatcher.getInstance();
        //录入
        dispatcher.controllerAppSendMsg(ProcessorType.CAPTCHA, EventType.CAPTCHA_CHECK,
                new TupleOf3<>(CaptchaMemDB.TP_FORGET_PWD_PAYMENT, phone, captcha))
                .thenCompose(e -> {
                    return dispatcher.controllerAppSendMsg(ProcessorType.USER, EventType.UPDATE_PRICE_PWD, user);
                })
                .thenAccept(e -> {
                    dr.setResult(JsonResult.SUCCESS_RESULT_JSON);
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.errWithData(rsp.getResult().getCode(), rsp.getData()));
                });
        return dr;
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/logout", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String logout(HttpServletRequest request){
        Long uid = JwtUtil.getUid(request);
        if (uid != null) {
            SessionMemDB.removeSession(uid);
        }
        return JsonResult.SUCCESS_RESULT_JSON;
    }
    
    /**
     * 流通记录
     * @param request
     * @param address
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/goods/circulateHistory", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> circulateHistory(HttpServletRequest request,
                                                   @RequestParam(value = "address", required = true) String address
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByPublicKey(address);
        if(gk == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
            return dr;
        }
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_GOODSID, gk.getGid())
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
    
    /**
     * 我的举报
     * @param request
     * @param count
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/goods/getAccusationHistoryList", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getAccusationHistoryList(HttpServletRequest request,
                                                           @RequestParam(value = "captcha", required = false) String captcha,
                                                           @RequestParam(value = "phone", required = false) String phone,
                                                           @RequestParam(value = "type", required = false) Integer type,
                                                           @RequestParam(value = "currentPage", required = false)Long currentPage,
                                                           @RequestParam(value = "count", required = false)Long count) {
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (currentPage == null) currentPage = Pager.START_PAGE;
        if (count == null) count =Pager.DEFAULT_COUNT;
        if(EmptyUtil.isNullOrEmpty(phone)){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_PARAM_PHONE_BE_NULL_ERROR));
            return dr;
        }
        if(captcha == null || captcha.length() > 6){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_CAPTCHA_INVALID));
            return dr;
        }
        Long start = (currentPage - 1) * count;
        map.put("type", type);
        map.put("phone", phone);
        map.put("currentPage", start);
        map.put("count", count);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_GET_ACCUSATION_HISTORY_LIST, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData(), rsp.getResult().getRecordTotal()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }

    /**
     * 查看商品明细
     * @param request
     * @param publicKey
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/goods/getGoodsDetailByCode", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> getGoodsDetailByCode(HttpServletRequest request,
                                                       @RequestParam(value = "publicKey", required = true) String publicKey
    ) {
        DeferredResult<String> dr = new DeferredResult<>();
        Map<String, Object> map = new HashMap<>();
        GoodsKey gk = GoodsKeyMemDB.getGoodsKeyByPublicKey(publicKey);
        if(gk == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.CODE_GOODS_KEY_NOT_EXISTS));
            return dr;
        }

        Goods goods = GoodsMemDB.getGoodsById(gk.getGid());
        if(goods == null){
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_USER_CODE_NOT));
            return dr;
        }

        Admin admin = AdminMemDB.getAdminByAid(goods.getAid());
        if(admin == null || admin.getType() != 2){
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_USER_CODE_NOT));
            return dr;
        }

        map.put("publicKey", publicKey);
        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.GOODS_DETAIL_CODE, map)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }

    /**
     * 新增举报
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/json/food/user/goods/addAccusation", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public DeferredResult<String> addAccusation(HttpServletRequest request,
                                                           @RequestParam(value = "type", required = true) Integer type,
                                                           @RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "identityCcard", required = false) String identityCcard,
                                                           @RequestParam(value = "phone", required = true) String phone,
                                                           @RequestParam(value = "content", required = false) String content,
                                                           @RequestParam(value = "publicKey", required = true) String publicKey,
                                                           @RequestParam(value = "urlA", required = false) String urlA,
                                                           @RequestParam(value = "url", required = false) List url
                                                           ) {
        DeferredResult<String> dr = new DeferredResult<>();
        AccusationHistory accusationHistory = new AccusationHistory();
        phone = StringUtils.trimToNull(phone);
        //identityCcard = StringUtils.trimToNull(identityCcard);
        /*if (null == identityCcard) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_CARD_NOT));
            return dr;
        }*/

        if (null == phone) {
            dr.setResult(JsonResult.errResultString(ErrMessage.USER_PHONE_NOT));
            return dr;
        }
        Long gid = GoodsKeyMemDB.getGoodsKeyByPublicKey(publicKey).getGid();
        accusationHistory.setGoodsid(gid);
        accusationHistory.setType(type);
        accusationHistory.setStatus(AccusationHistory.STATUS_NOT_DISPOSE);
        accusationHistory.setName(name);
        accusationHistory.setIdentityCcard(identityCcard);
        accusationHistory.setPhone(phone);
        accusationHistory.setContent(content);

        String filePath = "/home/dev/completition/food/img/";
        //String filePath1 = "wx.bitspace.link/img/";
        String filePath1 = "http://food2.bitzone.space/img/";
        boolean flag = true;
        if(url != null && url.size() > 0){
            for(int i = 0; i < url.size(); i++){
                switch(i){
                    case 1:
                        //accusationHistory.setUrlA(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlA(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 3:
                        //accusationHistory.setUrlB(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlB(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 5:
                        //accusationHistory.setUrlC(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlC(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 7:
                        //accusationHistory.setUrlD(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlD(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 9:
                        //accusationHistory.setUrlE(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlE(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 11:
                        //accusationHistory.setUrlF(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlF(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 13:
                        //accusationHistory.setUrlG(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlG(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                    case 15:
                        //accusationHistory.setUrlH(url.get(i).toString());
                        //保存图片
                        accusationHistory.setUrlH(filePath1+System.currentTimeMillis()+".jpg");
                        flag = PictureAnalysisUtil.GenerateImage(url.get(i).toString(),filePath+System.currentTimeMillis()+".jpg");
                        break;
                }
                if(!flag){
                    Result result = new Result(ErrMessage.CODE_IMG_SAVE_ERR);
                    dr.setResult(JsonResult.errResultString(result.getCode()));
                    return dr;
                }
            }
        }
        accusationHistory.setCreateTime(System.currentTimeMillis());

        Dispatcher.getInstance().controllerAppSendMsg(ProcessorType.GOODS, EventType.ADMIN_INSERT_ACCUSATION_HISTORY, accusationHistory)
                .thenAccept(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResultWrapper.succStr(rsp.getData(), rsp.getResult().getRecordTotal()));
                })
                .fail(e -> {
                    Response rsp = (Response) e.getData();
                    dr.setResult(JsonResult.errResultString(rsp.getResult().getCode()));
                });
        return dr;
    }
}
