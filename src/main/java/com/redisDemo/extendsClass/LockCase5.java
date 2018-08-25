package com.redisDemo.extendsClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redisDemo.abstractClass.RedisLock;
import com.redisDemo.util.LockConstants;
import com.redisDemo.util.RedisUtils;

import redis.clients.jedis.Jedis;

/**
 * 
 *                                        ,s555SB@@&                         
 *                                      :9H####@@@@@Xi                       
 *                                     1@@@@@@@@@@@@@@8                      
 *                                   ,8@@@@@@@@@B@@@@@@8                     
 *                                  :B@@@@X3hi8Bs;B@@@@@Ah,                  
 *             ,8i                  r@@@B:     1S ,M@@@@@@#8;                
 *            1AB35.i:               X@@8 .   SGhr ,A@@@@@@@@S               
 *            1@h31MX8                18Hhh3i .i3r ,A@@@@@@@@@5              
 *            ;@&i,58r5                 rGSS:     :B@@@@@@@@@@A              
 *             1#i  . 9i                 hX.  .: .5@@@@@@@@@@@1              
 *              sG1,  ,G53s.              9#Xi;hS5 3B@@@@@@@B1               
 *               .h8h.,A@@@MXSs,           #@H1:    3ssSSX@1                 
 *               s ,@@@@@@@@@@@@Xhi,       r#@@X1s9M8    .GA981              
 *               ,. rS8H#@@@@@@@@@@#HG51;.  .h31i;9@r    .8@@@@BS;i;         
 *                .19AXXXAB@@@@@@@@@@@@@@#MHXG893hrX#XGGXM@@@@@@@@@@MS       
 *                s@@MM@@@hsX#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&,     
 *              :GB@#3G@@Brs ,1GM@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@B,    
 *            .hM@@@#@@#MX 51  r;iSGAM@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@8    
 *          :3B@@@@@@@@@@@&9@h :Gs   .;sSXH@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@:   
 *      s&HA#@@@@@@@@@@@@@@M89A;.8S.       ,r3@@@@@@@@@@@@@@@@@@@@@@@@@@@r   
 *   ,13B@@@@@@@@@@@@@@@@@@@5 5B3 ;.         ;@@@@@@@@@@@@@@@@@@@@@@@@@@@i   
 *  5#@@#&@@@@@@@@@@@@@@@@@@9  .39:          ;@@@@@@@@@@@@@@@@@@@@@@@@@@@;   
 *  9@@@X:MM@@@@@@@@@@@@@@@#;    ;31.         H@@@@@@@@@@@@@@@@@@@@@@@@@@:   
 *   SH#@B9.rM@@@@@@@@@@@@@B       :.         3@@@@@@@@@@@@@@@@@@@@@@@@@@5   
 *     ,:.   9@@@@@@@@@@@#HB5                 .M@@@@@@@@@@@@@@@@@@@@@@@@@B   
 *           ,ssirhSM@&1;i19911i,.             s@@@@@@@@@@@@@@@@@@@@@@@@@@S  
 *              ,,,rHAri1h1rh&@#353Sh:          8@@@@@@@@@@@@@@@@@@@@@@@@@#: 
 *            .A3hH@#5S553&@@#h   i:i9S          #@@@@@@@@@@@@@@@@@@@@@@@@@A.
 *
 *
 * @author 厉昀键
 * create in 2018年8月24日
 * 在lockCase4的基础上为获得锁进程增加一个守护线程
 *
 */
public class LockCase5 extends RedisLock {
	
	private static Logger logger = LoggerFactory.getLogger(LockCase5.class);
	
	Jedis jedis = RedisUtils.getJedis();

	public LockCase5(Jedis jedis, String lockKey) {
		super(jedis, lockKey);
	}

	public void lock() {
		while (true) {
			logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 请求锁");
			String result = jedis.set(lockKey, String.valueOf(Thread.currentThread().getId()), LockConstants.NOT_EXIST,LockConstants.SECONDS,3);
			if (LockConstants.OK.equals(result)) {
				logger.info("线程 " + Thread.currentThread().getId() +" 加锁成功!");
				//开启定时刷新过期时间
				isOpenExpirationRenewal = true;
				scheduleExpirationRenewal(Thread.currentThread());
				break;
			}else {
				logger.info("线程 " + jedis.get(lockKey) + " 获得锁剩余时间为: " + jedis.ttl(lockKey));
				logger.info("线程 :"+Thread.currentThread().getId() + " 获取锁失败，休眠2秒!");
				//休眠10秒
				sleepBySencond(2);
			}
		}
	}

	public void unlock() {
		String checkAndDelScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
				"return redis.call('del', KEYS[1]) " +
				"else " +
				"return 0 " +
				"end";
		jedis.eval(checkAndDelScript, 1, lockKey, String.valueOf(Thread.currentThread().getId()));
		isOpenExpirationRenewal = false;
		logger.info("线程 " + String.valueOf(Thread.currentThread().getId()) + " 解锁成功");
	}

}
