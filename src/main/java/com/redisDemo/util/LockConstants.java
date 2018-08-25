package com.redisDemo.util;

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
 * create in 2018年8月20日
 * 类说明
 * redis分布式锁常量类
 */
public class LockConstants {
	
	/**是否获得锁标记*/
	public static final String OK = "OK";
	/**
	 * redis 用法 
	 * NX|XX, NX -- Only set the key if it does not already exist. 
	 * XX -- Only set the key if it already exist. 
	 * **/
	public static final String NOT_EXIST = "NX";
	public static final String EXIST = "XX";
	/** redis 存值时间设置
	 * expx EX|PX, expire time units: EX = seconds; 
	 * PX = milliseconds 
	 * **/
	public static final String SECONDS = "EX";
	public static final String MILLISECONDS = "PX";
	/**构造函数*/
	public LockConstants(){}
}
