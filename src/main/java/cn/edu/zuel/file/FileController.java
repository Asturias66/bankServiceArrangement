package cn.edu.zuel.file;

import cn.edu.zuel.common.intercepter.AuthInterceptor;
import cn.edu.zuel.common.intercepter.CorsInterceptor;
import cn.edu.zuel.common.module.File;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Path("/file")
@ValidateParam
public class FileController extends Controller {

    @Inject
    FileService fileService;

    // 先接到，处理文件，再处理别的逻辑
    @Before(CorsInterceptor.class)
    @Clear(AuthInterceptor.class)
//    @Param(name = "fileNames")
    public void upload(String file) {
        UploadFile document;
        if (file!=null){
            document = getFile(file);
            System.out.println(getRawData());
        }else {
            document = getFile();
        }
//        UploadFile document = getFile();
        // 获得上传文件的原始文件名
        String fileName = document.getFileName();
        System.out.println("文件名:" + fileName);
        // 获得文件的后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 重命名上传的文件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String lastThreeLetter = fileName.substring(fileName.lastIndexOf("."));
        String newName = sdf.format(new Date()) + (int) (Math.random() * 10000) + lastThreeLetter;
        // 文件的存储路径，这里存储相对路径
        String savedPath = "\\src\\main\\resources\\file\\upfile";
        // 构建document实体并赋值
        File document1 = new File();
        document1.setOriginalName(fileName);
        document1.setNewName(newName);
        document1.setSavedPath(savedPath);
        document1.setSuffix(suffix);
//        document1.setUserId(getAttr("ACCOUNT_ID"));
        HashMap<String, Object> data = new HashMap<>();
        if (!document1.save()) {
            renderJson(BaseResult.fail("文件上传失败"));
            return;
        }
        data.put("fileId", document1.getId());
        data.put("filePath", PathKit.getWebRootPath() + savedPath);
        renderJson(DataResult.data(data));

    }

    /**
     * 文件下载操作，直接用绝对路径传给前端
     */
    @Clear(AuthInterceptor.class)
    @Param(name = "fileId", required = true)
    public void downLoad(BigInteger fileId) {
        File document = fileService.getById(fileId);
        System.out.println(PathKit.getWebRootPath());
        renderFile(new java.io.File(PathKit.getWebRootPath() + "/src/main/resources/file/upfile/" + document.getOriginalName()));
    }

    @Clear(AuthInterceptor.class)
    @Param(name = "imgStr",required = true)
    public void generateImage(String imgStr){
        if (imgStr == null) // 图像数据为空
            return;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            String h = imgStr.split(",")[1];
            byte[] b = decoder.decodeBuffer(h);
            for (int i = 0; i < b.length; i++) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
            String newName = sdf.format(new Date()) + (int) (Math.random() * 10000)+".png";
            String name = HashKit.md5(newName);*/
            String savedPath = "code1/file/upfile";
            String imgFilePath = savedPath+"/"+"m.png";
            // 生成jpg图片
            FileOutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            renderJson(BaseResult.ok());
            return;
        } catch (Exception e) {
            renderJson(BaseResult.fail());
            return;
        }
    }


}
