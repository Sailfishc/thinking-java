/*
 * Created on 12-9-24
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Copyright @2012 the original author or authors.
 */
package chapter02_demo;

import java.util.HashMap;
import java.util.Map;

/**
 * Description of this file.
 * User: Xiong Neng
 * Date: 12-9-24
 * Time: 下午5:01
 */
public class Test {
    public static final Map<String,String> A = new HashMap<String,String>();

    public static void main(String[] args) {
//        int[] intArray = new int[]{1,2,3};
//        Class<Test> clazz = Test.class;
//        try {
//            System.out.println(clazz.getDeclaredField("A").getName());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
        // 1 1111 1111
        int a = 0x1FF;
        // 1 1111 1111
        // 0 0000 0000
        System.out.println(a >> 2);

    }
}
