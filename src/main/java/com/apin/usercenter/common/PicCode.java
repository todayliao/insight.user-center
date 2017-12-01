package com.apin.usercenter.common;

import com.apin.util.Generator;
import com.apin.util.encrypt.Base64Encryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2017/11/3
 * @remark
 */
@Component()
public class PicCode {
    @Autowired
    private StringRedisTemplate redis;

    /**
     * 生成验证问题图片
     *
     * @param mobile 手机号
     * @return Base64编码的验证问题图片
     * @throws IOException
     */
    public String generateCode(String tokenId, String mobile) throws IOException {
        Random random = new Random();

        // 初始化参数
        int width = 77, height = 22;
        String[] ops = {"+", "-", "="};

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setColor(Color.GRAY);

        // 添加干扰线
        for (int i = 0; i < 40; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(10);
            int y2 = random.nextInt(10);
            g.drawLine(x1, y1, x1 + x2, y1 + y2);
        }

        // 设置字体
        Font font = new Font("黑体", Font.BOLD, 18);
        g.setFont(font);

        // 生成验证问题图片
        int num1 = random.nextInt(90);
        String strRand1 = String.valueOf(num1);
        int red1 = random.nextInt(255);
        int green1 = random.nextInt(255);
        int blue1 = random.nextInt(255);
        g.setColor(new Color(red1, green1, blue1));
        g.drawString(strRand1, 6, 16);

        int opNum = random.nextInt(2);
        String strRand2 = ops[opNum];
        g.setColor(new Color(0, 0, 0));
        g.drawString(strRand2, 13 * 2 + 6, 16);

        int num2 = random.nextInt(9);
        String strRand3 = String.valueOf(num2);
        int red3 = random.nextInt(255);
        int green3 = random.nextInt(255);
        int blue3 = random.nextInt(255);
        g.setColor(new Color(red3, green3, blue3));
        g.drawString(strRand3, 13 * 3 + 6, 16);

        String strRand4 = ops[2];
        g.setColor(new Color(0, 0, 0));
        g.drawString(strRand4, 13 * 4 + 6, 16);

        // 计算验证答案
        Integer randomCode = 0;
        switch (opNum) {
            case 0:
                randomCode = num1 + num2;
                break;
            case 1:
                randomCode = num1 - num2;
                break;
            default:
                break;
        }

        buffImg.flush();
        g.dispose();

        // 用手机号和验证答案的Hash值作为Key,手机号为Value,保存到Redis
        String key = Generator.md5(mobile + randomCode);
        String parentKey = Generator.md5(tokenId + "pic");
        String lastKey = redis.opsForValue().get(parentKey);
        if (lastKey != null && !lastKey.isEmpty() && redis.hasKey(lastKey)) {
            redis.delete(lastKey);
        }
        redis.opsForValue().set(key, mobile, 5, TimeUnit.MINUTES);
        redis.opsForValue().set(parentKey, key, 5, TimeUnit.MINUTES);

        // 返回Base64编码的验证问题图片
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(buffImg, "jpeg", output);
        output.close();
        return Base64Encryptor.encode(output.toByteArray());
    }
}
