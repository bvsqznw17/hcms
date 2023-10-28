package com.ruoyi.connDev.modbus;

import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.ParamValueMapper;

public class MbServerThread extends Thread {

    private Socket socket = null;
    private String ip = null;
    private String devName = null;// 与服务端通信的设备名称
    private String uuid = null; // 与服务器通信的设备uuid
    private List<RegLib> regLibs = null;

    private static final String KEY_PREFIX = "mb:";
    private static HashMap<String, String> lastStoreData = new HashMap<>();

    public MbServerThread(Socket socket, String ip, String devName, List<RegLib> regLibs) {
        this.socket = socket;
        this.devName = devName;
        this.ip = ip;
        this.regLibs = regLibs;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void run() {

        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);

        // 删除相同IP但不同设备名称的数据
        deleteDifferentDevNameData();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 创建一个定时任务，延迟1秒后开始执行，每2秒执行一次
        scheduler.scheduleAtFixedRate(new RedisReadTask_pre(socket, regLibs), 1, 20, TimeUnit.SECONDS);

        // 存储redis采集的数据
        lastStoreData = getKeyValuePairsWithPrefix(KEY_PREFIX, redisCache);

        // 再创建一个定时任务，延迟1秒后开始执行，每5秒执行一次
        scheduler.scheduleAtFixedRate(new UpdateMbDataToDbTask(devName, regLibs, lastStoreData), 1, 50,
                TimeUnit.SECONDS);

    }

    // 删除相同IP但不同设备名称的数据
    private void deleteDifferentDevNameData() {
        ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);
        ParamValue pv_ip = new ParamValue();
        pv_ip.setIp(ip);
        List<ParamValue> paramValues = paramValueMapper.selectParamValueList(pv_ip);
        for (ParamValue paramValue : paramValues) {
            if (!paramValue.getDevName().equals(devName)) {
                paramValueMapper.deleteParamValueByDevName(paramValue.getDevName());
            }
        }
    }

    // 获取具有指定前缀的键值对
    public static HashMap<String, String> getKeyValuePairsWithPrefix(String prefix, RedisCache redisCache) {
        HashMap<String, String> map = new HashMap<>();
        // 先获取具有指定前缀的keys
        Collection<String> keys = redisCache.keys(prefix);
        for (String key : keys) {
            map.put(key, redisCache.getCacheObject(key).toString());
        }
        return map;
    }

    // 比较两个map并返回差异集合
    public static HashMap<String, String> compareMaps(HashMap<String, String> map1,
            HashMap<String, String> map2) {
        HashMap<String, String> diff = new HashMap<>();

        for (String key : map1.keySet()) {
            if (map2.containsKey(key) && !map1.get(key).equals(map2.get(key))) {

                diff.put(key, map1.get(key));
            }
        }
        return diff;
    }

}
