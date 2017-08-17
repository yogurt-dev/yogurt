package pub.utils;

import com.github.jyoghurt.security.securityUserT.domain.CertT;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Security;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: pub.utils
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-01-21 09:59
 */
public class BaseCert {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(BaseCert.class);
    /**
     * BouncyCastleProvider
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public BaseCert() {

    }

    /**
     * 生成数字证书，主方法
     */
    public void excute(CertT certT){
        generateClientCert(certT);
        generateClientCer(certT);
        setTrust(certT);
        setTrusty(certT);
    }


    /**
     * 生成客户端证书
     *
     * @param certT 证书信息
     */
    public void generateClientCert(CertT certT) {
        //命令
        String commandStr = "";
        commandStr = commandStr.concat("openssl genrsa -des3 -passout pass:").concat(SysVarEnum.SECURITY_CA_STOREPASS
                .getCode()).concat(" -out ").concat(certT.getPath()).concat(certT.getAlias()).concat(".key 1024");
        logger.debug("创建电子证书，命令1："+commandStr);
        exeCommand(commandStr);
    }

    /**
     * 生成客户端cer文件
     *
     * @param certT 证书信息
     */
    public void generateClientCer(CertT certT) {
        String command = "";
        command = command.concat("openssl req -new -passin pass:").concat(certT.getPassword()).concat(" -subj ")
                .concat("/C=").concat(certT.getC()).concat("/ST=").concat(certT.getSt()).concat("/L=").concat(certT
                        .getL()).concat("/O=").concat(certT.getO()).concat("/OU=").concat(certT.getOu()).concat
                        ("/CN=").concat(certT.getCn()).concat("/emailAddress=server@lvyushequ.com ").concat("-key ")
                .concat(certT.getPath()).concat(certT.getAlias()).concat(".key ").concat(
                        "-out ")
                .concat(certT.getPath()).concat(certT.getAlias()).concat(".csr");
        logger.debug("创建电子证书，命令2：" + command);
        exeCommand(command);
    }

    /**
     * 设置证书信任
     *
     * @param certT     证书信息
     */
    public void setTrust(CertT certT) {
        //命令
        String commandStr = "";
        commandStr = commandStr.concat("openssl ca -in ").concat(certT.getPath()).concat(certT.getAlias()).concat("" +
                ".csr").concat(" -cert ").concat(certT.getCpath()).concat("ca.crt -keyfile ").concat(certT.getCpath()
        ).concat("ca.key -out ").concat(certT.getPath()).concat(certT.getAlias()).concat(".crt -config " +
                "'/usr/local/nginx/ca/conf/openssl.conf'  -batch");
        logger.debug("创建电子证书，命令3："+commandStr);
        exeCommand(commandStr);
    }

    /**
     * 设置证书信任
     *
     * @param certT     证书信息
     */
    public void setTrusty(CertT certT) {
        //命令
        String commandStr = "";
        commandStr = commandStr.concat("openssl pkcs12 -passin pass:").concat(certT.getPassword()).concat(" -passout pass:").concat(certT.getClientPwd()).concat(" -export -clcerts -in ").concat(certT
                .getPath()).concat(certT.getAlias()).concat(".crt -inkey ").concat(certT.getPath()).concat(certT
                .getAlias())
                .concat
                (".key " +
                "-out ").concat(certT.getPath()).concat(certT.getAlias()).concat(".p12");
        logger.debug("创建电子证书，命令4："+commandStr);
        exeCommand(commandStr);
    }

    /**
     * 执行命令行命令
     *
     * @param commandStr
     */
    private void exeCommand(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);

            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            logger.debug(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    public static void main(String args[]) {
//        BaseCert baseCert = new BaseCert();
//        CertT certT = new CertT();
//        certT.setAlias("123123");
//        certT.setC(SysVarEnum.SECURITY_CA_C.getCode());
//        certT.setCn("123123");
//        certT.setL(SysVarEnum.SECURITY_CA_L.getCode());
//        certT.setO(SysVarEnum.SECURITY_CA_O.getCode());
//        certT.setOu(SysVarEnum.SECURITY_CA_OU.getCode());
//        certT.setSt(SysVarEnum.SECURITY_CA_ST.getCode());
//        certT.setPassword(SysVarEnum.SECURITY_CA_STOREPASS.getCode());
//        certT.setSerialNumber(new BigInteger("123123"));
//        certT.setPath("/usr/local/nginx/ca/users/");
//        certT.setClientPwd("123123");
//        certT.setCpath("/usr/local/nginx/ca/private/");
//        baseCert.generateClientCert(certT);
//        baseCert.generateClientCer(certT);
//        baseCert.setTrust(certT);
//        baseCert.setTrusty(certT);
//    }

}
