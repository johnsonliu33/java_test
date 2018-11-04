package com.bitspace.food.handler;

import com.bitspace.food.base.*;
import com.bitspace.food.config.SystemConfig;
import com.bitspace.food.controller.db.SessionContext;
import com.bitspace.food.controller.db.SessionContextUtil;
import com.bitspace.food.disruptor.Dispatcher;
import com.bitspace.food.disruptor.annotation.EventMethod;
import com.bitspace.food.disruptor.impl.ResponseHandler;
import com.bitspace.food.disruptor.inf.AnnotatedHandler;


import com.bitspace.food.entity.*;
import com.bitspace.food.mapper.*;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.memdb.GoodsKeyMemDB;
import com.bitspace.food.memdb.GoodsMemDB;
import com.bitspace.food.memdb.UserMoneyMemDB;
import com.bitspace.food.util.*;
import com.google.gson.internal.LinkedTreeMap;
import com.offbynull.coroutines.user.Continuation;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * All rights Reserved, Designed By www.bitzone.zone
 *
 * @package_name com.bitspace.food.handler
 * @class_name
 * @auth Administrator
 * @create_time 2018/5/25 15:16
 * @company 香港币特空间交易平台有限公司
 * @comments
 * @method_name
 * @return Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GoodsHandler implements AnnotatedHandler {
    private static Logger log = LoggerFactory.getLogger(GoodsHandler.class);
    
    /**
     *  商品管理列表
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GOODS_LIST)
    public void goodsList(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            long total = mapper.goodsListCount(map);
            if (total != 0) {
                dataMap = mapper.goodsList(map);
            }
            responseHandler.sendResponse(req, Result.success(total), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  查询商品详情
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GOODS_INFO)
    public void goodsInfoById(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        Map<String,Object> dataMap = new HashMap<>();
        try {
            Long goodsId = (Long) req.getData();
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            LinkedHashMap<String,Object> info = mapper.goodsInfoByIdMap(goodsId);
            List<LinkedHashMap<String,Object>> memoLists = mapper.getMemo(goodsId);
            LinkedHashMap<String,Object> goodsReports = mapper.getGoodsReports(goodsId);
            dataMap.put("info",info);
            dataMap.put("memoLists",memoLists);
            dataMap.put("goodsReports",goodsReports);
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  修改流通密码
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GOODS_UPDATE_PAYPWD)
    public void updatePayPwd(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            GoodsKey gk = (GoodsKey) req.getData();
            context = new SessionContext();
            GoodsKeyMapper mapper = context.getMapper(GoodsKeyMapper.class);
            mapper.updatePayPwd(gk);
            context.commit();
            GoodsKeyMemDB.putGoodsKey(gk);
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  添加memo
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GOODS_INSET_MEMO)
    public void insertMemo(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            Long goodsId = Long.parseLong(map.get("goodsId").toString());
            Integer type = Integer.parseInt(map.get("type").toString());
            Long aid = Long.parseLong(map.get("aid").toString());
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Long sellAid = mapper.getGoodsStatusAndSellAid(goodsId);
            if(Admin.TYPE_SUPPLIER.equals(type)){
                if(sellAid != null){
                    result = new Result(ErrMessage.CODE_PARAM_GOODS_YET_OUT);
                    return;
                }
            }else if(Admin.TYPE_DEALER.equals(type)){
                if(!aid.equals(sellAid)){
                    result = new Result(ErrMessage.CODE_PARAM_GOODS_NOT_OUT);
                    return;
                }
            }
            //memo上链
            Memo memo = new Memo();
            memo.setAid(Long.parseLong(map.get("aid").toString()));
            memo.setGoodsid(Long.parseLong(map.get("goodsId").toString()));
            memo.setContent(map.get("content").toString());
            memo.setCreateTime(System.currentTimeMillis());
            Event eventMemo = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS_MEMO, memo, req, continuation);
            Response rspMemo = (Response)eventMemo.getData();
            if(!rspMemo.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hashMemo = rspMemo.getData().toString();
            map.put("hashId",hashMemo);

            mapper.insetMemo(map);
            context.commit();
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  添加商品
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GOODS_INSET)
    public void addGoods(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Pair<Goods, String> pair = (Pair<Goods, String>) req.getData();
            Goods goods = pair.getLeft();
            String payPwd = pair.getRight();
            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);

            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            mapper.insetGoods(goods);
            
            //生成key
            String privateKey = KeyUtil.generatePrivateKey(payPwd+goods.getId());
            Event event = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_WALLET, privateKey, req, continuation);
            Response rsp = (Response)event.getData();
            if(!rsp.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String publicKey = String.valueOf(rsp.getData());
            Map<String,Object> param = new HashMap<>();
            param.put("fromaddr",SystemConfig.getJwtCnf().getSourceAddress());
            param.put("fromsecret",SystemConfig.getJwtCnf().getSourcePassword());
            param.put("toaddr",publicKey);
            param.put("value",goods.getNumber());
            Event event1 = responseHandler.sendRequest(ProcessorType.GOODS, EventType.WALLET_TRANSFER, param, req, continuation);
            Response rsp1 = (Response)event1.getData();
            if(!rsp.getResult().isSuccess()){
                responseHandler.sendResponse(req, rsp1.getResult());
                return;
            }
            GoodsKey goodsKey = new GoodsKey(goods.getId(),payPwd,privateKey,publicKey);
            GoodsKeyMapper gkmapper = context.getMapper(GoodsKeyMapper.class);
            gkmapper.insertGoodsKey(goodsKey);
            
            context.commit();

            //写入缓存
            GoodsMemDB.putGoods(goods);
            GoodsKeyMemDB.putGoodsKey(goodsKey);


            GoodsKeyMemDB.putGoodsKey(goodsKey);
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("goodsId",goods.getId());
            responseHandler.sendResponse(req, Result.success(),dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     * 钱包上链
     * @return
     */
    @EventMethod(EventType.CREATE_WALLET)
    private void registerRpc1(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        String account_id = null;
        try {
            String addressAPwd = (String) req.getData();
            String httpUrl = SystemConfig.getJwtCnf().getRegisterRpcUrl();
            Map<String,Object> data = new HashMap<>();
            data.put("passphrase",addressAPwd);
            String httpResult = HttpUtil.doPost(httpUrl, data);
            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_WITHDRAW_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            account_id = String.valueOf(resultTree.get("account_id"));
            result = Result.success();
            /*responseHandler.sendResponse(req, Result.success(), account_id);*/
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result,account_id);
        }
    }
    
    /**
     *  完善商品
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_PERFECT_GOODS)
    public void perfectGoods(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Goods goods = (Goods) req.getData();

            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);

            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            mapper.updateGoods(goods);
            context.commit();

            //写入缓存
            GoodsMemDB.putGoods(goods);

            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  审核商品
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_AUDIT_GOODS)
    public void auditGoods(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Pair<GoodsReports, Integer> pair = (Pair<GoodsReports, Integer>) req.getData();
            GoodsReports goodsReports = pair.getLeft();
            Integer status = pair.getRight();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            //TODO 如做缓存处理时需要更改此处处理
            Goods goods = GoodsMemDB.getGoodsById(goodsReports.getGid());
            //goods.setId(goodsReports.getGid());
            goods.setStatus(status);
            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);
            mapper.updateGoods(goods);

            //写入缓存
            GoodsMemDB.putGoods(goods);

            //商品检验记录上链
            Event eventGoodsReports = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS_REPORTS, goodsReports, req, continuation);
            Response rspGoodsReports = (Response)eventGoodsReports.getData();
            if(!rspGoodsReports.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash1 = rspGoodsReports.getData().toString();
            goodsReports.setHashId(hash1);
            mapper.insertGoodsReports(goodsReports);

            context.commit();
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  监管局召回
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_RECALL_GOODS)
    public void recallGoods(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Map<String, Object> map = (Map<String, Object>) req.getData();
            //TODO 如做缓存处理时需要更改此处处理
            Long goodsId = Long.parseLong(String.valueOf(map.get("goodsId")));
            Goods goods = mapper.goodsInfoById(goodsId);
            goods.setId(Long.parseLong(String.valueOf(map.get("goodsId"))));
            goods.setStatus(Integer.parseInt(String.valueOf(map.get("status"))));
            goods.setCause(String.valueOf(map.get("content")));
            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);
            mapper.updateGoods(goods);
            //写入缓存
            GoodsMemDB.putGoods(goods);
            
            List<Long> aids = new ArrayList<>();
            aids.add(goods.getAid());
            if(goods.getSellAid() != null) {
                aids.add(goods.getSellAid());
            }
            AdminMapper adminMapper = context.getMapper(AdminMapper.class);
            adminMapper.batchUpdateRecallNum(aids);
            //memo上链
            Memo memo = new Memo();
            memo.setAid(Long.parseLong(map.get("aid").toString()));
            memo.setGoodsid(Long.parseLong(map.get("goodsId").toString()));
            memo.setContent(map.get("content").toString());
            memo.setCreateTime(System.currentTimeMillis());
            Event eventMemo = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS_MEMO, memo, req, continuation);
            Response rspMemo = (Response)eventMemo.getData();
            if(!rspMemo.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hashMemo = rspMemo.getData().toString();
            map.put("hashId",hashMemo);
            mapper.insetMemo(map);
            context.commit();
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    
    /**
     *  供应商转出
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_SUPPLIER_OUT)
    public void supplierOut(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Tuple<CirculateHistory, String> reqData = (Tuple<CirculateHistory, String>) req.getData();
            CirculateHistory circulateHistory = reqData.getFirst();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Goods goods = mapper.goodsInfoById(circulateHistory.getGoodsId());
            //判断数量是否为最大数量
            if(!circulateHistory.getAmount().equals(goods.getNumber())){
                result = new Result(ErrMessage.CODE_PARAM_NUMBER_ALL_ERROR);
                return;
            }
            Map<String,Object> param = new HashMap<>();
            param.put("fromaddr",circulateHistory.getFromAddress());
            param.put("fromsecret",circulateHistory.getPrivateKey());
            param.put("toaddr",circulateHistory.getPeerAddress());
            param.put("value",circulateHistory.getAmount());
            Event event = responseHandler.sendRequest(ProcessorType.GOODS, EventType.WALLET_TRANSFER, param, req, continuation);
            Response rsp = (Response)event.getData();
            if(!rsp.getResult().isSuccess()){
                responseHandler.sendResponse(req, rsp.getResult());
                return;
            }
            goods.setCirculateNum(circulateHistory.getAmount());
            goods.setSellAid(circulateHistory.getPeerAid());

            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);

            //修改商品信息
            mapper.updateGoods(goods);

            //商品流通记录上链
            Event eventCirculateHistory = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS_HISTORY, circulateHistory, req, continuation);
            Response rspCirculateHistory = (Response)eventCirculateHistory.getData();
            if(!rspCirculateHistory.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hashHistory = rspCirculateHistory.getData().toString();
            circulateHistory.setHashId(hashHistory);

            //插入流通记录
            CirculateHistoryMapper circulateHistoryMapper = context.getMapper(CirculateHistoryMapper.class);
            circulateHistoryMapper.insert(circulateHistory);
            context.commit();
            //写入缓存
            GoodsMemDB.putGoods(goods);
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  经销商转出
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_DEALER_OUT)
    public void dealerOut(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            CirculateHistory circulateHistory = (CirculateHistory)req.getData();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Goods goods = mapper.goodsInfoById(circulateHistory.getGoodsId());
            //流通数量必须大于转出数量
            int circulateNum = goods.getCirculateNum() - circulateHistory.getAmount();
            if(circulateNum < 0){
                result = new Result(ErrMessage.CODE_PARAM_NUMBER_ERROR);
                return;
            }
            Map<String,Object> param = new HashMap<>();
            param.put("fromaddr",circulateHistory.getPeerAddress());
            param.put("fromsecret",circulateHistory.getPrivateKey());
            param.put("toaddr",circulateHistory.getFromAddress());
            param.put("value",circulateHistory.getAmount());
            Event event = responseHandler.sendRequest(ProcessorType.GOODS, EventType.WALLET_TRANSFER, param, req, continuation);
            Response rsp = (Response)event.getData();
            if(!rsp.getResult().isSuccess()){
                responseHandler.sendResponse(req, rsp.getResult());
                return;
            }
            goods.setCirculateNum(circulateNum);
            goods.setRecallNum(goods.getRecallNum()+circulateHistory.getAmount());

            //商品数据上链
            Event eventGoods = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS, goods, req, continuation);
            Response rspGoods = (Response)eventGoods.getData();
            if(!rspGoods.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hash = rspGoods.getData().toString();
            goods.setHashId(hash);
            //修改商品信息
            mapper.updateGoods(goods);

            //插入流通记录
            CirculateHistoryMapper circulateHistoryMapper = context.getMapper(CirculateHistoryMapper.class);
            circulateHistory.setFromAid(goods.getAid());
            //商品流通记录上链
            Event eventCirculateHistory = responseHandler.sendRequest(ProcessorType.GOODS, EventType.CREATE_GOODS_HISTORY, circulateHistory, req, continuation);
            Response rspCirculateHistory = (Response)eventCirculateHistory.getData();
            if(!rspCirculateHistory.getResult().isSuccess()){
                responseHandler.sendResponse(req, new Result(ErrMessage.CODE_DB_ERROR));
                return;
            }
            String hashHistory = rspCirculateHistory.getData().toString();
            circulateHistory.setHashId(hashHistory);
            circulateHistoryMapper.insert(circulateHistory);

            context.commit();

            //写入缓存
            GoodsMemDB.putGoods(goods);

            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     * 钱包上链
     * @return
     */
    @EventMethod(EventType.WALLET_TRANSFER)
    private void sendXrp(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        try {
            Map<String,Object> data = (Map<String,Object>)req.getData();
            String httpUrl = SystemConfig.getJwtCnf().getSendxrpUrl();
            String httpResult = HttpUtil.doPost(httpUrl, data);
            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult && !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_TRANSFER_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            String status =  String.valueOf(resultTree.get("status"));
            if("success".equals(status)){
                result = Result.success();
            }else{
                result = new Result(ErrMessage.CODE_HTTP_TRANSFER_FAIL);
            }
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  流通记录
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_GOODSID)
    public void getCircuLateHistoryListByGoodsId(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        List<LinkedHashMap<String, Object>> circulateHistoryDataMap = new ArrayList<>();
        try {
            Long goodsId = (Long) req.getData();
            Long aid = GoodsMemDB.getGoodsById(goodsId).getAid();
            Admin admin = AdminMemDB.getAdminByAid(aid);
            if(admin == null || admin.getType() != 2){
                result = new Result(ErrMessage.USER_USER_CODE_NOT);
                responseHandler.sendResponse(req, result);
                return;
            }
            context = new SessionContext();
            CirculateHistoryMapper mapper = context.getMapper(CirculateHistoryMapper.class);
            dataMap = mapper.getCircuLateHistoryListByGoodsId(goodsId);
            if(dataMap != null && dataMap.size() > 0){
                for(int i = 0; i < dataMap.size(); i++){
                    LinkedHashMap<String, Object> circulateHistoryMap = new LinkedHashMap<String, Object>();
                    Long id = Long.parseLong(dataMap.get(i).get("id").toString());
                    String hashStr = dataMap.get(i).get("hashId").toString();
                    String httpUrl = "http://test.vac.zone/vac/getransaction";
                    Map<String,Object> data = new HashMap<>();
                    data.put("tx_id",hashStr);
                    String httpResult = HttpUtil.doPost(httpUrl, data);

                    JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
                    if(null == jsonResult || !jsonResult.isSuccess()){
                        result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
                    }
                    LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
                    LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
                    String blockId = resultTree.get("ledger_index").toString();//区块ID
                    List list = (List<LinkedTreeMap<String,Object>>)resultTree.get("Memos");
                    LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)list.get(0);
                    LinkedTreeMap<String,Object> resultTree2 = (LinkedTreeMap<String,Object>)resultTree1.get("Memo");
                    String memoData = String.valueOf(resultTree2.get("MemoData"));
                    CirculateHistory circulateHistory = (CirculateHistory)PictureAnalysisUtil.readObject(memoData);
                    circulateHistoryMap.put("id",id);
                    circulateHistoryMap.put("goodsId",circulateHistory.getGoodsId());
                    circulateHistoryMap.put("fromAid",circulateHistory.getFromAid());
                    circulateHistoryMap.put("fromName",AdminMemDB.getAdminByAid(circulateHistory.getFromAid()).getName());
                    circulateHistoryMap.put("fromAddress",circulateHistory.getFromAddress());
                    circulateHistoryMap.put("peerAid",circulateHistory.getPeerAid());
                    circulateHistoryMap.put("peerName",AdminMemDB.getAdminByAid(circulateHistory.getPeerAid()).getName());
                    circulateHistoryMap.put("peerAddress",circulateHistory.getPeerAddress());
                    circulateHistoryMap.put("amount",circulateHistory.getAmount());
                    circulateHistoryMap.put("type",circulateHistory.getType());
                    circulateHistoryMap.put("createTime",circulateHistory.getCreateTime());
                    circulateHistoryMap.put("hashId",hashStr);
                    circulateHistoryMap.put("blockId",blockId.substring(0,blockId.length()-2));
                    circulateHistoryDataMap.add(circulateHistoryMap);
                }
            }
            if(circulateHistoryDataMap != null && circulateHistoryDataMap.size() > 0){
                responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
            }else{
                result = new Result(ErrMessage.USER_USER_CODE_NOT_REPORTS_HISTORY);
                responseHandler.sendResponse(req, result);
            }
            //responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.USER_USER_CODE_NOT_REPORTS_HISTORY);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     *  流通记录(交易码查)
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_HASHID)
    public void getCircuLateHistoryListByHashId(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        List<LinkedHashMap<String, Object>> circulateHistoryDataMap = new ArrayList<>();
        try {
            String hashId = (String) req.getData();
            context = new SessionContext();
            CirculateHistoryMapper mapper = context.getMapper(CirculateHistoryMapper.class);
            dataMap = mapper.getCircuLateHistoryListByHashId(hashId);
            if(dataMap != null && dataMap.size() > 0){
                for(int i = 0; i < dataMap.size(); i++){
                    LinkedHashMap<String, Object> circulateHistoryMap = new LinkedHashMap<String, Object>();
                    Long id = Long.parseLong(dataMap.get(i).get("id").toString());
                    String hashStr = dataMap.get(i).get("hashId").toString();
                    String httpUrl = "http://test.vac.zone/vac/getransaction";
                    Map<String,Object> data = new HashMap<>();
                    data.put("tx_id",hashStr);
                    String httpResult = HttpUtil.doPost(httpUrl, data);

                    JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
                    if(null == jsonResult || !jsonResult.isSuccess()){
                        result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
                    }
                    LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
                    LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
                    String ledgerIndex = resultTree.get("ledger_index").toString();//区块ID
                    List list = (List<LinkedTreeMap<String,Object>>)resultTree.get("Memos");
                    LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)list.get(0);
                    LinkedTreeMap<String,Object> resultTree2 = (LinkedTreeMap<String,Object>)resultTree1.get("Memo");
                    String memoData = String.valueOf(resultTree2.get("MemoData"));
                    CirculateHistory circulateHistory = (CirculateHistory)PictureAnalysisUtil.readObject(memoData);
                    circulateHistoryMap.put("id",id);
                    circulateHistoryMap.put("goodsId",circulateHistory.getGoodsId());
                    circulateHistoryMap.put("fromAid",circulateHistory.getFromAid());
                    circulateHistoryMap.put("fromName",AdminMemDB.getAdminByAid(circulateHistory.getFromAid()).getName());
                    circulateHistoryMap.put("fromAddress",circulateHistory.getFromAddress());
                    circulateHistoryMap.put("peerAid",circulateHistory.getPeerAid());
                    circulateHistoryMap.put("peerName",AdminMemDB.getAdminByAid(circulateHistory.getPeerAid()).getName());
                    circulateHistoryMap.put("peerAddress",circulateHistory.getPeerAddress());
                    circulateHistoryMap.put("amount",circulateHistory.getAmount());
                    circulateHistoryMap.put("type",circulateHistory.getType());
                    circulateHistoryMap.put("createTime",circulateHistory.getCreateTime());
                    circulateHistoryMap.put("hashId",hashStr);
                    circulateHistoryMap.put("ledgerIndex",ledgerIndex.substring(0,ledgerIndex.length()-2));
                    circulateHistoryDataMap.add(circulateHistoryMap);
                }
            }

            if(circulateHistoryDataMap != null && circulateHistoryDataMap.size() > 0){
                responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
            }else{
                result = new Result(ErrMessage.USER_USER_CODE_NOT_REPORTS);
                responseHandler.sendResponse(req, result);
            }

            //responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     *  最新流通记录
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_CIRCULATE_HISTORY_LIST_BY_NEW)
    public void getCircuLateHistoryListNew(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        List<LinkedHashMap<String, Object>> circulateHistoryDataMap = new ArrayList<>();
        try {
            String hashId = (String) req.getData();
            context = new SessionContext();
            CirculateHistoryMapper mapper = context.getMapper(CirculateHistoryMapper.class);
            dataMap = mapper.getCircuLateHistoryListNew();
            if(dataMap != null && dataMap.size() > 0){
                for(int i = 0; i < dataMap.size(); i++){
                    LinkedHashMap<String, Object> circulateHistoryMap = new LinkedHashMap<String, Object>();
                    Long id = Long.parseLong(dataMap.get(i).get("id").toString());
                    String hashStr = dataMap.get(i).get("hashId").toString();
                    String httpUrl = "http://test.vac.zone/vac/getransaction";
                    Map<String,Object> data = new HashMap<>();
                    data.put("tx_id",hashStr);
                    String httpResult = HttpUtil.doPost(httpUrl, data);

                    JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
                    if(null == jsonResult || !jsonResult.isSuccess()){
                        result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
                    }
                    LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
                    LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
                    String ledgerIndex = resultTree.get("ledger_index").toString();//区块ID
                    List list = (List<LinkedTreeMap<String,Object>>)resultTree.get("Memos");
                    LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)list.get(0);
                    LinkedTreeMap<String,Object> resultTree2 = (LinkedTreeMap<String,Object>)resultTree1.get("Memo");
                    String memoData = String.valueOf(resultTree2.get("MemoData"));
                    CirculateHistory circulateHistory = (CirculateHistory)PictureAnalysisUtil.readObject(memoData);
                    circulateHistoryMap.put("id",id);
                    circulateHistoryMap.put("goodsId",circulateHistory.getGoodsId());
                    circulateHistoryMap.put("fromAid",circulateHistory.getFromAid());
                    circulateHistoryMap.put("fromName",AdminMemDB.getAdminByAid(circulateHistory.getFromAid()).getName());
                    circulateHistoryMap.put("fromAddress",circulateHistory.getFromAddress());
                    circulateHistoryMap.put("peerAid",circulateHistory.getPeerAid());
                    circulateHistoryMap.put("peerName",AdminMemDB.getAdminByAid(circulateHistory.getPeerAid()).getName());
                    circulateHistoryMap.put("peerAddress",circulateHistory.getPeerAddress());
                    circulateHistoryMap.put("amount",circulateHistory.getAmount());
                    circulateHistoryMap.put("type",circulateHistory.getType());
                    circulateHistoryMap.put("createTime",circulateHistory.getCreateTime());
                    circulateHistoryMap.put("hashId",hashStr);
                    circulateHistoryMap.put("ledgerIndex",ledgerIndex.substring(0,ledgerIndex.length()-2));
                    circulateHistoryDataMap.add(circulateHistoryMap);
                }
            }

            /*if(circulateHistoryDataMap != null && circulateHistoryDataMap.size() > 0){
                responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
            }else{
                result = new Result(ErrMessage.USER_USER_CODE_NOT_REPORTS_HISTORY);
                responseHandler.sendResponse(req, result);
            }*/
            responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);

            //responseHandler.sendResponse(req, Result.success(), circulateHistoryDataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  获取举报列表
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_GET_ACCUSATION_HISTORY_LIST)
    public void getAccusationHistoryList(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        try {
            Map<String, Object> map = (Map<String, Object>) req.getData();
            context = new SessionContext();
            AccusationHistoryMapper mapper = context.getMapper(AccusationHistoryMapper.class);
            long total = mapper.getListCount(map);
            if (total != 0) {
                dataMap = mapper.getList(map);
            }
            responseHandler.sendResponse(req, Result.success(total), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  批量处理举报
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_BANCH_DISPOSE_ACCUSATION)
    public void banchDisposeAccusation(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Map<String, Object> map = (Map<String, Object>) req.getData();
            AccusationHistoryMapper mapper = context.getMapper(AccusationHistoryMapper.class);
            mapper.banchDispose(map);
            context.commit();
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  供应商首页统计
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_SUPPLIER_INDEX_COUNT)
    public void supplierIndexCount(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Long aid = (Long) req.getData();
            Map<String,Integer> dataMap = new HashMap<>();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Integer createNum = mapper.countNumByAid(aid);
            Integer recallNum = mapper.countNumByAidAndStatus(aid,Goods.STATUS_RECALL);
            dataMap.put("createNum",createNum);
            dataMap.put("recallNum",recallNum);
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  经销商首页统计
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_DEALER_INDEX_COUNT)
    public void dealerIndexCount(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Long sellAid = (Long) req.getData();
            Map<String,Integer> dataMap = new HashMap<>();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Integer saleNum = mapper.countNumBySellAid(sellAid);
            Integer recallNum = mapper.countNumBySellAidAndStatus(sellAid,Goods.STATUS_RECALL);
            dataMap.put("saleNum",saleNum);
            dataMap.put("recallNum",recallNum);
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }
    
    /**
     *  监管部门首页统计
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_MONITOR_INDEX_COUNT)
    public void monitorIndexCount(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        try {
            context = new SessionContext();
            Map<String,Integer> dataMap = new HashMap<>();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            Integer createNum = mapper.countNum();
            Integer auditNum = mapper.countNumByStatus(Goods.STATUS_CHECK_PENDING);
            AccusationHistoryMapper accusationHistoryMapper = context.getMapper(AccusationHistoryMapper.class);
            Integer accusationNum = accusationHistoryMapper.countByStatus(AccusationHistory.STATUS_NOT_DISPOSE);
            dataMap.put("createNum",createNum);
            dataMap.put("auditNum",auditNum);
            dataMap.put("accusationNum",accusationNum);
            responseHandler.sendResponse(req, Result.success(), dataMap);
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     *  查看商品明细
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.GOODS_DETAIL_CODE)
    public void getGoodsDetailByCode(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        LinkedHashMap<String, Object> dataMap1 = new LinkedHashMap<String, Object>();
        try {
            Map<String, Object> map = (Map) req.getData();
            context = new SessionContext();
            GoodsMapper mapper = context.getMapper(GoodsMapper.class);
            //dataMap = mapper.getGoodsDetailByCode(map);


            //查看商品明细
            String goodsHash = "";
            goodsHash = mapper.getGoodsHashByPublicKey(map);
            String httpUrl = "http://test.vac.zone/vac/getransaction";
            Map<String,Object> data = new HashMap<>();
            data.put("tx_id",goodsHash);
            String httpResult = HttpUtil.doPost(httpUrl, data);

            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            String goodsBlockId = resultTree.get("ledger_index").toString();//区块ID
            List list = (List<LinkedTreeMap<String,Object>>)resultTree.get("Memos");
            LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)list.get(0);
            LinkedTreeMap<String,Object> resultTree2 = (LinkedTreeMap<String,Object>)resultTree1.get("Memo");
            String memoData = String.valueOf(resultTree2.get("MemoData"));
            Goods goods = (Goods)PictureAnalysisUtil.readObject(memoData);
            dataMap1.put("goodsBlockId",goodsBlockId.substring(0,goodsBlockId.length()-2));
            dataMap1.put("createTime",goods.getCreateTime());
            dataMap1.put("amount",goods.getNumber());
            dataMap1.put("publicKey",map.get("publicKey"));
            dataMap1.put("licence",goods.getLicence());
            dataMap1.put("brand",goods.getBrand());
            dataMap1.put("specification",goods.getSpecification());
            dataMap1.put("burdenSheet",goods.getBurdenSheet());
            dataMap1.put("store",goods.getStore());
            dataMap1.put("expirationDate",goods.getExpirationDate());
            dataMap1.put("worksName",goods.getWorksName());
            dataMap1.put("worksAddress",goods.getWorksAddress());
            dataMap1.put("worksPhone",goods.getWorksPhone());
            dataMap1.put("createTime",goods.getCreateTime());
            dataMap1.put("status",goods.getStatus());
            dataMap1.put("cause",goods.getCause());
            dataMap1.put("goodsData",memoData);

            //查看检验明细
            String reportHash = mapper.getReportsHashByPublicKey(map);
            if(reportHash != null){
                String httpReportsUrl = "http://test.vac.zone/vac/getransaction";
                Map<String,Object> reportsData = new HashMap<>();
                reportsData.put("tx_id",reportHash);
                String httpReportsResult = HttpUtil.doPost(httpReportsUrl, reportsData);

                JsonResultWrapper jsonReportsResult = JsonUtil.fromJson(httpReportsResult, JsonResultWrapper.class);
                if(null == jsonReportsResult || !jsonReportsResult.isSuccess()){
                    result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
                }
                LinkedTreeMap<String,Object> resultReportsData = (LinkedTreeMap<String,Object>)jsonReportsResult.getData();
                LinkedTreeMap<String,Object> resultReportsTree = (LinkedTreeMap<String,Object>)resultReportsData.get("result");
                List listReports = (List<LinkedTreeMap<String,Object>>)resultReportsTree.get("Memos");
                LinkedTreeMap<String,Object> resultReportsTree1 = (LinkedTreeMap<String,Object>)listReports.get(0);
                LinkedTreeMap<String,Object> resultReportsTree2 = (LinkedTreeMap<String,Object>)resultReportsTree1.get("Memo");

                String memoReportsData = String.valueOf(resultReportsTree2.get("MemoData"));
                GoodsReports goodsReports = (GoodsReports)PictureAnalysisUtil.readObject(memoReportsData);
                dataMap1.put("facade",goodsReports.getFacade());
                dataMap1.put("moisture",goodsReports.getMoisture());
                dataMap1.put("impurity",goodsReports.getImpurity());
                dataMap1.put("unsoundGrain",goodsReports.getUnsoundGrain());
                dataMap1.put("oleaginousness",goodsReports.getOleaginousness());
                dataMap1.put("aflatoxin",goodsReports.getAflatoxin());
                dataMap1.put("gongContent",goodsReports.getGongContent());
                dataMap1.put("hchResidual",goodsReports.getHchResidual());
                dataMap1.put("ddtResidual",goodsReports.getDdtResidual());
                dataMap1.put("defectiveParticle",goodsReports.getDefectiveParticle());
                dataMap1.put("annualOutput",goodsReports.getAnnualOutputOf());
                dataMap1.put("placeOrigin",goodsReports.getPlaceOfOrigin());
                dataMap1.put("goodsReportsData",memoReportsData);
            }

            //链上查看memo
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<LinkedHashMap<String, Object>> dataMemoList = new ArrayList<>();
            dataMemoList = mapper.getMemoHashListByPublicKey(map);
            LinkedHashMap<String, Object> dataMap2 = new LinkedHashMap<String, Object>();
            LinkedHashMap<String, Object> dataMap3 = new LinkedHashMap<String, Object>();
            List list2 = new ArrayList();
            List list3 = new ArrayList();//存放16进制字符串
            if(dataMemoList != null && dataMemoList.size() > 0 && dataMemoList.get(0) != null){
                for(int i = 0; i < dataMemoList.size(); i++){
                    Long aid = Long.parseLong(dataMemoList.get(i).get("aid").toString());
                    Long gid = Long.parseLong(dataMemoList.get(i).get("gid").toString());
                    LinkedHashMap<String, Object> memoMap = new LinkedHashMap<String, Object>();
                    String moHash = dataMemoList.get(i).get("hashId").toString();
                    String httpMoUrl = "http://test.vac.zone/vac/getransaction";
                    Map<String,Object> moData = new HashMap<>();
                    moData.put("tx_id",moHash);
                    String httpMoResult = HttpUtil.doPost(httpMoUrl, moData);

                    JsonResultWrapper jsonMoResult = JsonUtil.fromJson(httpMoResult, JsonResultWrapper.class);
                    if(null == jsonMoResult || !jsonMoResult.isSuccess()){
                        result = new Result(ErrMessage.CODE_HTTP_GOODS_SELECT_FAIL);
                    }
                    LinkedTreeMap<String,Object> resultMoData = (LinkedTreeMap<String,Object>)jsonMoResult.getData();
                    LinkedTreeMap<String,Object> resultMoTree = (LinkedTreeMap<String,Object>)resultMoData.get("result");
                    List listMo = (List<LinkedTreeMap<String,Object>>)resultMoTree.get("Memos");
                    LinkedTreeMap<String,Object> resultMoTree1 = (LinkedTreeMap<String,Object>)listMo.get(0);
                    LinkedTreeMap<String,Object> resultMoTree2 = (LinkedTreeMap<String,Object>)resultMoTree1.get("Memo");

                    String memoMoData = String.valueOf(resultMoTree2.get("MemoData"));
                    Memo memos = (Memo)PictureAnalysisUtil.readObject(memoMoData);
                    Admin admin = AdminMemDB.getAdminByAid(aid);
                    Goods good = GoodsMemDB.getGoodsById(gid);
                    /*if(admin != null){
                        if(admin.getType() == 1){
                            list2.add("管理员："+memos.getContent());
                            list3.add(memoMoData);
                        }else if(admin.getType() == 2){
                            list2.add("供应商："+memos.getContent());
                            list3.add(memoMoData);
                        }else{
                            list2.add("经销商："+memos.getContent());
                            list3.add(memoMoData);
                        }
                    }*/

                    if(admin != null){
                        Date date = new Date(memos.getCreateTime());
                        memoMap.put("name",admin.getName());
                        memoMap.put("content",memos.getContent());
                        memoMap.put("creatTime",simpleDateFormat.format(date));
                        list2.add(memoMap);
                        //list2.add(admin.getName()+"  "+memos.getContent()+"  "+ simpleDateFormat.format(date) + "/n");
                        list3.add(memoMoData);
                    }
                }
            }
            dataMap2.put("memo",list2);
            dataMap3.put("memo16",list3);

            dataMap.add(dataMap1);
            dataMap.add(dataMap2);
            dataMap.add(dataMap3);

            if(dataMap != null && dataMap.size() > 0){
                responseHandler.sendResponse(req, Result.success(), dataMap);
            }else{
                result = new Result(ErrMessage.USER_USER_CODE_NOT);
                responseHandler.sendResponse(req, result);
            }


            result = Result.success();

        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     *  新增举报
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_INSERT_ACCUSATION_HISTORY)
    public void addAccusation(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        try {
            AccusationHistory accusationHistory = (AccusationHistory) req.getData();
            context = new SessionContext();
            AccusationHistoryMapper mapper = context.getMapper(AccusationHistoryMapper.class);
            int num = mapper.insertAccusationHistory(accusationHistory);
            GoodsMapper goodsMapper = context.getMapper(GoodsMapper.class);
            Goods goods = goodsMapper.goodsInfoById(accusationHistory.getGoodsid());
            List<Long> aids = new ArrayList<>();
            aids.add(goods.getAid());
            if(goods.getSellAid() != null) {
                aids.add(goods.getSellAid());
            }
            AdminMapper adminMapper = context.getMapper(AdminMapper.class);
            adminMapper.batchUpdateReportNum(aids);
            context.commit();
            responseHandler.sendResponse(req, Result.success());
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result);
        }
    }

    /**
     * 商品数据上链
     * @return
     */
    @EventMethod(EventType.CREATE_GOODS)
    private void addGoodsToChain(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        String hash = null;
        try {
            Goods goods = (Goods) req.getData();
            ByteArrayOutputStream byt=new ByteArrayOutputStream();
            ObjectOutputStream obj=new ObjectOutputStream(byt);
            obj.writeObject(goods);
            byte[] bytes=byt.toByteArray();
            String strGoods = PictureAnalysisUtil.bytesToHexString(bytes);

            String httpUrl = SystemConfig.getJwtCnf().getAddGoodsToChainUrl();
            Map<String,Object> data = new HashMap<>();
            data.put("fromaddr","rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh");
            data.put("fromsecret","snoPBrXtMeMyMHUVTgbuqAfg1SUTb");
            data.put("toaddr","rH1sAYpAcYkF8yinoJ6vGc8MbVzLZXox3x");
            data.put("value","1000");
            data.put("memotype","422d");
            data.put("memodata",strGoods);
            String httpResult = HttpUtil.doPost(httpUrl, data);

            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_GOODS_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("tx_json");
            hash = String.valueOf(resultTree1.get("hash"));
            result = Result.success();
            /*responseHandler.sendResponse(req, Result.success(), account_id);*/
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result,hash);
        }
    }

    /**
     * 商品记录上链
     * @return
     */
    @EventMethod(EventType.CREATE_GOODS_REPORTS)
    private void addGoodsReportsToChain(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        String hash = null;
        try {
            GoodsReports goodsReports = (GoodsReports) req.getData();
            ByteArrayOutputStream byt=new ByteArrayOutputStream();
            ObjectOutputStream obj=new ObjectOutputStream(byt);
            obj.writeObject(goodsReports);
            byte[] bytes=byt.toByteArray();
            String strGoodsReports = PictureAnalysisUtil.bytesToHexString(bytes);

            String httpUrl = SystemConfig.getJwtCnf().getAddGoodsToChainUrl();
            Map<String,Object> data = new HashMap<>();
            data.put("fromaddr","rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh");
            data.put("fromsecret","snoPBrXtMeMyMHUVTgbuqAfg1SUTb");
            data.put("toaddr","rH1sAYpAcYkF8yinoJ6vGc8MbVzLZXox3x");
            data.put("value","1000");
            data.put("memotype","422d");
            data.put("memodata",strGoodsReports);
            String httpResult = HttpUtil.doPost(httpUrl, data);

            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_GOODS_REPORTS_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("tx_json");
            hash = String.valueOf(resultTree1.get("hash"));
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result,hash);
        }
    }

    /**
     * 商品流通记录上链
     * @return
     */
    @EventMethod(EventType.CREATE_GOODS_HISTORY)
    private void addGoodsHistoryToChain(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        String hash = null;
        try {
            CirculateHistory circulateHistory = (CirculateHistory) req.getData();
            ByteArrayOutputStream byt=new ByteArrayOutputStream();
            ObjectOutputStream obj=new ObjectOutputStream(byt);
            obj.writeObject(circulateHistory);
            byte[] bytes=byt.toByteArray();
            String strGoodsReports = PictureAnalysisUtil.bytesToHexString(bytes);

             String httpUrl = SystemConfig.getJwtCnf().getAddGoodsToChainUrl();
            Map<String,Object> data = new HashMap<>();
            data.put("fromaddr","rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh");
            data.put("fromsecret","snoPBrXtMeMyMHUVTgbuqAfg1SUTb");
            data.put("toaddr","rH1sAYpAcYkF8yinoJ6vGc8MbVzLZXox3x");
            data.put("value","1000");
            data.put("memotype","422d");
            data.put("memodata",strGoodsReports);
            String httpResult = HttpUtil.doPost(httpUrl, data);

            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_GOODS_REPORTS_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("tx_json");
            hash = String.valueOf(resultTree1.get("hash"));
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result,hash);
        }
    }

    /**
     * memo上链
     * @return
     */
    @EventMethod(EventType.CREATE_GOODS_MEMO)
    private void addMemoToChain(Request req, ResponseHandler responseHandler, Continuation continuation){
        Result result = null;
        String hash = null;
        try {
            Memo memo = (Memo) req.getData();
            ByteArrayOutputStream byt=new ByteArrayOutputStream();
            ObjectOutputStream obj=new ObjectOutputStream(byt);
            obj.writeObject(memo);
            byte[] bytes=byt.toByteArray();
            String strGoodsReports = PictureAnalysisUtil.bytesToHexString(bytes);

            String httpUrl = SystemConfig.getJwtCnf().getAddGoodsToChainUrl();
            Map<String,Object> data = new HashMap<>();
            data.put("fromaddr","rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh");
            data.put("fromsecret","snoPBrXtMeMyMHUVTgbuqAfg1SUTb");
            data.put("toaddr","rH1sAYpAcYkF8yinoJ6vGc8MbVzLZXox3x");
            data.put("value","1000");
            data.put("memotype","422d");
            data.put("memodata",strGoodsReports);
            String httpResult = HttpUtil.doPost(httpUrl, data);

            JsonResultWrapper jsonResult = JsonUtil.fromJson(httpResult, JsonResultWrapper.class);
            if(null == jsonResult || !jsonResult.isSuccess()){
                result = new Result(ErrMessage.CODE_HTTP_GOODS_MEMO_FAIL);
            }
            LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.getData();
            LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
            LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("tx_json");
            hash = String.valueOf(resultTree1.get("hash"));
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            responseHandler.sendResponse(req, result,hash);
        }
    }

    /**
     *  区块高度
     *
     * @param req
     * @param responseHandler
     * @param continuation
     */
    @EventMethod(EventType.ADMIN_BLOCK_HEIGHT)
    public void getBlockHeight(Request req, ResponseHandler responseHandler, Continuation continuation) {
        SessionContext context = null;
        Result result = null;
        LinkedHashMap<String, Object> dataMap1 = new LinkedHashMap<String, Object>();
        List<LinkedHashMap<String, Object>> listMap = new ArrayList<>();
        List<LinkedHashMap<String, Object>> listMap1 = new ArrayList<>();
        try {
            listMap = blockIndex();
            dataMap1 = browserIndex();
            dataMap1.put("size",listMap.size());
            for(int i = 0; i < listMap.size(); i++){
                if(listMap1.size() >= 10){
                    break;
                }
                listMap1.add(listMap.get(listMap.size() - i - 1));
            }
            dataMap1.put("list",listMap1);
            result = Result.success();
        } catch (Exception e) {
            LoggerUtil.error(log, e);
            result = new Result(ErrMessage.CODE_DB_ERROR);
        } finally {
            SessionContextUtil.closeSilently(context);
            responseHandler.sendResponse(req, result, dataMap1);
        }
    }

    /**
     * 区块链浏览器 首页统计
     * @return
     */
    private LinkedHashMap<String, Object> browserIndex() throws Exception{
        LinkedHashMap<String, Object> restluMap = new LinkedHashMap<String, Object>();
        String httpUrl = "http://me.vac.zone/api/ledger?ledger_index=validated";
        String httpResult = HttpUtil.doGet(httpUrl);
        LinkedTreeMap<String,Object> resultData = JsonUtil.fromJson(httpResult, LinkedTreeMap.class);
        LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
        LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("result");
        LinkedTreeMap<String,Object> resultTree2 = (LinkedTreeMap<String,Object>)resultTree1.get("ledger");
        restluMap.put("hash",resultTree2.get("hash"));
        restluMap.put("ledgerIndex",resultTree2.get("ledger_index"));
        restluMap.put("startTime","2018-Jun-11 08:29:15 ");
        restluMap.put("closeTime",resultTree2.get("close_time_human"));
        return restluMap;
    }

    private List<LinkedHashMap<String, Object>> blockIndex() throws Exception{
        List<LinkedHashMap<String, Object>> dataMap = new ArrayList<>();
        List<LinkedHashMap<String, Object>> circulateHistoryDataMap = new ArrayList<>();
        String httpUrl = "http://me.vac.zone/api/ledger?ledger_index=validated&full=full";
        String httpResult = HttpUtil.doGet(httpUrl);
        LinkedTreeMap<String,Object> jsonResult = JsonUtil.fromJson(httpResult, LinkedTreeMap.class);
        LinkedTreeMap<String,Object> resultData = (LinkedTreeMap<String,Object>)jsonResult.get("result");
        LinkedTreeMap<String,Object> resultTree = (LinkedTreeMap<String,Object>)resultData.get("result");
        LinkedTreeMap<String,Object> resultTree1 = (LinkedTreeMap<String,Object>)resultTree.get("ledger");
        List<LinkedTreeMap<String,Object>> list = (List<LinkedTreeMap<String,Object>>)resultTree1.get("accountState");
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        for(int i = 0; i < list.size(); i++){
            LinkedHashMap<String, Object> map1 = new LinkedHashMap<String, Object>();
            if(list.get(i).get("PreviousTxnLgrSeq") == null){
                continue;
            }
            if(list.get(i).get("Account") == null){
                continue;
            }
            map1.put("blockId", list.get(i).get("PreviousTxnLgrSeq"));
            map1.put("account",list.get(i).get("Account"));
            circulateHistoryDataMap.add(map1);
        }
        return circulateHistoryDataMap;
    }
}

