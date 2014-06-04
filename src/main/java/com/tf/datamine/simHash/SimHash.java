package com.tf.datamine.simHash;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimHash {
	
	//要计算simhash值得文本
    private String tokens;
    //得到的simhash值
     BigInteger intSimHash;
    
    private String strSimHash;
    //默认的simhash位数
    private int hashbits = 64;

    public SimHash(String tokens) {
        this.tokens = tokens;
        this.intSimHash = this.simHash();
    }

    public SimHash(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.intSimHash = this.simHash();
    }

    public BigInteger simHash() {
    	// 定义特征向量/数组
        int[] v = new int[this.hashbits];
        //用于切分字符串 
        StringTokenizer stringTokens = new StringTokenizer(this.tokens);
        //遍历每一个单词
        while (stringTokens.hasMoreTokens()) {
            String temp = stringTokens.nextToken();
            //1。得先干掉停用次等
            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(temp);
            for (int i = 0; i < this.hashbits; i++) {
            	// 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
				// 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
				// 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
            	//可以理解成没个词的权重都为1
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                //signum  符号位，负数是为-1，零时为0，正数是为1
                // 这里是应该计算整个文档的所有特征的向量和
				// 这里实际使用中需要 +- 权重，比如词频，而不是简单的 +1/-1，
                 if (t.and(bitmask).signum() != 0) {
                    v[i] += 1; 
                } else {
                    v[i] -= 1;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        StringBuffer simHashBuffer = new StringBuffer();
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
            }else{
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        System.out.println(this.strSimHash + " length " + this.strSimHash.length());
        return fingerprint;
    }
  //计算hash
    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(
                    new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }
    
    /**
     * 取两个二进制的异或，统计为1的个数，就是海明距离
     * @param other
     * @return
     */

    public int hammingDistance(SimHash other) {
    	
        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;
        
        //统计x中二进制位数为1的个数
        //我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，对吧，然后，n&(n-1)就相当于把后面的数字清0，
        //我们看n能做多少次这样的操作就OK了。
        
         while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    /** 
     * calculate Hamming Distance between two strings 
     *  二进制怕有错，当成字符串，作一个，比较下结果
     * @author  
     * @param str1 the 1st string 
     * @param str2 the 2nd string 
     * @return Hamming Distance between str1 and str2 
     */  
    public int getDistance(String str1, String str2) {  
        int distance;  
        if (str1.length() != str2.length()) {  
            distance = -1;  
        } else {  
            distance = 0;  
            for (int i = 0; i < str1.length(); i++) {  
                if (str1.charAt(i) != str2.charAt(i)) {  
                    distance++;  
                }  
            }  
        }  
        return distance;  
    }
    
    /**
     * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用
     * @param simHash
     * @param distance
     * @return
     */
    public List<BigInteger> subByDistance(SimHash simHash, int distance){
    	int numEach = this.hashbits/(distance+1);
    	List<BigInteger> characters = new ArrayList();
        
    	StringBuffer buffer = new StringBuffer();

    	int k = 0;
    	for( int i = 0; i < this.intSimHash.bitLength(); i++){
        	boolean sr = simHash.intSimHash.testBit(i);
        	
        	if(sr){
        		buffer.append("1");
        	}	
        	else{
        		buffer.append("0");
        	}
        	
        	if( (i+1)%numEach == 0 ){
            	BigInteger eachValue = new BigInteger(buffer.toString(),2);
            	System.out.println("----" +eachValue );
            	buffer.delete(0, buffer.length());
            	characters.add(eachValue);
        	}
        }

    	return characters;
    }
}