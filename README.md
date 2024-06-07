# License

软件产品控制License的简单实现，通过控制部署服务器的CPU序列号和MAC地址(或自定义控制参数)进行校验  

环境：jdk1.8
## 一、license创建工具使用说明

### 1、代码
(1)  创建license工具---> licensecreate-utils

### 2、使用方式
(1) com.license.tools.licensecreate.utils.KeyGenerator  main方法生成RSA秘钥对

(2) com.license.tools.licensecreate.utils.RSAUtils 静态变量填入步骤一生成的公私秘钥，main方法填入AES加密需要的明文密码，运行得到加密的AES秘钥

(3) 将步骤二得到的AES秘钥填入com.license.tools.licensecreate.utils.AESUtils 静态变量aesEncyptPwd

(4) 运行com.license.tools.licensecreate.test 包下CreateSign.main()方法，依次填入以下参数：  
① Mac地址+cpu序列号（或自定义控制参数）拼接的加密串（使用license-util工具com.license.tools.license.utils.LicenseManager 的getSystemSign()方法生成）  
② 输入软件生效起始时间  
③ 输入软件生效截止时间  
④ 输入软件上一次校验时间初始值  
⑤ 输入软件部署唯一版本号（不能带“-”）  
⑥ 输入license文件生成路径  
执行成功，license文件生成在自定义输入的文件路径下。

## 二、license SDK包使用说明

### 1、license-util 提供三个方法及一个守护线程（定时校验）
(1) validate() 验证方法：校验部署服务器的mac地址，cpu序列号，过期时间  
(2) getSystemSign()方法：获取部署服务器的服务器标识加密串（在客户机调用生成后在本地生成license文件）  
(3) updateSign()方法：更新授权码（重复license生成步骤，将新的license文件内容更新到客户机的license文件）  
(4) 提供一个服务启动时的守护线程LicenseThread，循环验证license文件，并缓存结果。  
(5)配置文件增加参数配置： xxy.checkTime  = 3600    
（此参数是控制线程LicenseThread的校验频率，多久校验一次，单位/秒）

###2、使用步骤：
(1) 将工具打包，放到maven私库后，使用的服务pom文件加入私库地址，添加依赖
```java
<dependency>
    <groupId>com.license.tools</groupId>
    <artifactId>license-utils</artifactId>
    <version>1.0-RELEASE</version>
</dependency>
```

(2) 将生成的license.xml文件放在工作目录下（与jar包同级）  
(3) 在登录接口上添加获取验证结果处理逻辑（或使用拦截器）
```java
ValidateResult validateResult = LicenseThread.validateResult.get("Authorize");
if (!validateResult.getIsValidate()){
    retMap.put("code",validateResult.getCode());
    retMap.put("Msg",validateResult.getMessage());
    return retMap;
}
```

(4) 编写获取服务器标识接口（获取结果用于生成license文件的输入① ）
```java
@AutoWired
private LicenseManager licenseManager;
@GetMapping("/getServerID")
public Map<String,Object> getServerID(){
    Map<String,Object> retMap=new HashMap<>(2);
    retMap.put("code","200");
    retMap.put("serverID",licenseManager.getSystemSign());
    return retMap;
}
```

(5) 编写授权码更新接口
```java
@PostMapping("/updateSign")
public Map<String,Object> updateSign(String sign){
    Map<String,Object> retMap=new HashMap<>(2);
    licenseManager.updateSign(sign);
    retMap.put("code","200");
    retMap.put("msg","激活成功！");
    return retMap;
}
```

(6) 激活（生成新的授权码）  
步骤二-2-(4) 获取到的服务器标识在 步骤一-2-(4)-① 生成新的license文件，取license的内容（即授权码）在二-2-(5)进行激活。  

###3、自定义控制参数
（1）目前代码里面示例控制参数为 param1+param2，如果需要自定义或添加参数，可以在MySystemUtils中进行定义  
（2）自定义参数之后在LicenseManager类中的todo处进行相关参数解析和获取

<span style="color:red;">
注意：
</span>  
<span style="font-size: 15px">
1、获取cpu序列号时，实际是通过执行命令“dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1”获取，在docker中运行服务，如果找不到dmidecode 命令，需要绑定硬件信息配置到容器内
 docker 挂载目录增加 
- /dev/mem:/dev/mem
- /sbin/dmidecode:/sbin/dmidecode
- /usr/sbin/dmidecode:/usr/sbin/dmidecode 

2、docker网络使用非宿主机网络时，docker内的MAC地址会随着docker的重启改变，导致之前生成的授权码校验不通过。处理措施有以下几种：  
(1) docker容器内使用宿主机的网络“--net=host   --privileged=true ”，则mac地址一直跟随宿主机  
(2) docker启动命令添加指定mac地址“ --mac-address=xx:xx:xx:xx:xx:xx”  
</span>