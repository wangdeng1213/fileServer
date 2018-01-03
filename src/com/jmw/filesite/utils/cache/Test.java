package com.jmw.filesite.utils.cache;

import com.jmw.filesite.utils.AesCBC;


public class Test {
   public static void main(String[] args) {
	  String ss= "MEdutkN4wykxcfjvpQ0kSX+VeVW37UsyHmRGMMuwF7MfiJ8wSaIAkplB7QtJLnp+71hZJ31jduuxnc3FbPfObmuHQN8OGlCoJBqyUesaCc8=";
      try {
		System.out.println(new AesCBC().Decrypt(ss));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
}
