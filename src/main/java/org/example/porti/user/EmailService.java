package org.example.porti.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${server.frontUrl}")
    private String frontUrl;

    @Async
    public void sendWelcomeMail(String uuid, String email){
        MimeMessage message  = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            String subject = "[Poticard] 신규 가입 이메일 인증 안내";
            String backurl = System.getenv("BACK_URL");
            String htmlContents = "<!DOCTYPE html>\n" +
                    "<html lang=\"ko\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>포티카드 이메일 인증</title>\n" +
                    "    <style>\n" +
                    "        /* 모바일 대응을 위한 기본 스타일 Reset */\n" +
                    "        body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }\n" +
                    "        table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; border-collapse: collapse !important; }\n" +
                    "        body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; font-family: 'Noto Sans KR', 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif; background-color: #f4f7f9; color: #333333; }\n" +
                    "        a { text-decoration: none; }\n" +
                    "\n" +
                    "        /* 모바일 폰트 사이즈 조정 */\n" +
                    "        @media screen and (max-width: 600px) {\n" +
                    "            .content-table { width: 100% !important; border-radius: 0px !important; }\n" +
                    "            .header-text { font-size: 24px !important; }\n" +
                    "            .body-text { font-size: 15px !important; line-height: 1.6 !important; }\n" +
                    "            .btn-verify { width: 100% !important; padding: 15px 0 !important; font-size: 16px !important; }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "        <tr>\n" +
                    "            <td align=\"center\" style=\"padding: 40px 10px;\">\n" +
                    "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" class=\"content-table\" style=\"background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); overflow: hidden;\">\n" +
                    "                    \n" +
                    "                    <tr>\n" +
                    "                        <td align=\"center\" style=\"padding: 30px 40px 10px 40px; background-color: #ffffff; border-bottom: 1px solid #f0f0f0;\">\n" +
                    "                            <div class=\"logo-text\" style=\"font-size: 24px; font-weight: 800; color: #1a73e8; letter-spacing: -1px;\">\n" +
                    "                                PORTI <span style=\"font-weight: 300; color: #5f6368;\">CARD</span>\n" +
                    "                            </div>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "\n" +
                    "                    <tr>\n" +
                    "                        <td style=\"padding: 40px 50px 20px 50px;\">\n" +
                    "                            <h1 class=\"header-text\" style=\"margin: 0 0 20px 0; font-size: 28px; font-weight: 700; color: #202124; line-height: 1.3; letter-spacing: -0.5px;\">\n" +
                    "                                <span style=\"color: #1a73e8;\">반갑습니다!</span><br/>\n" +
                    "                                이메일 주소를 인증해 주세요.\n" +
                    "                            </h1>\n" +
                    "                            <p class=\"body-text\" style=\"margin: 0; font-size: 16px; font-weight: 400; line-height: 1.7; color: #5f6368;\">\n" +
                    "                                안녕하세요, 포티카드에 가입해 주셔서 진심으로 감사드립니다.<br/>\n" +
                    "                                <br/>\n" +
                    "                                요청하신 이메일 인증을 완료하기 위해 아래 버튼을 클릭해 주세요.<br/>\n" +
                    "                                버튼을 클릭하시면 즉시 가입 완료 및 서비스 이용이 가능합니다.\n" +
                    "                            </p>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "\n" +
                    "                    <tr>\n" +
                    "                        <td align=\"center\" style=\"padding: 20px 50px 40px 50px;\">\n" +
                    "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                    "                                <tr>\n" +
                    "                                    <td align=\"center\" style=\"border-radius: 8px;\" bgcolor=\"#1a73e8\">\n" +
                    "                                        <a href=\""+frontUrl+"api/user/verify?uuid="+uuid+"\" \n" +
                    "                                           target=\"_blank\" \n" +
                    "                                           class=\"btn-verify\"\n" +
                    "                                           style=\"display: inline-block; padding: 18px 60px; font-size: 17px; font-weight: 700; color: #ffffff; text-align: center; background-color: #1a73e8; border-radius: 8px; border: 1px solid #1a73e8; -webkit-font-smoothing: antialiased;\">\n" +
                    "                                            이메일 인증 완료하기\n" +
                    "                                        </a>\n" +
                    "                                    </td>\n" +
                    "                                </tr>\n" +
                    "                            </table>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "\n" +
                    "                    <tr>\n" +
                    "                        <td style=\"padding: 0px 50px 40px 50px; border-top: 1px solid #f0f0f0; background-color: #fafbfd;\">\n" +
                    "                            <p style=\"margin: 20px 0 0 0; font-size: 13px; color: #9aa0a6; line-height: 1.5;\">\n" +
                    "                                만약 위의 버튼이 작동하지 않거나 보이지 않는다면,<br/>\n" +
                    "                                아래 링크를 복사하여 브라우저 주소창에 붙여넣어 주세요.<br/>\n" +
                    "                                <br/>\n" +
                    "                                <span style=\"word-break: break-all; color: #1a73e8; text-decoration: underline;\">\n" +
                    "                                    "+frontUrl+"api/user/verify?uuid="+ uuid +"\n" +
                    "                                </span>\n" +
                    "                            </p>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "\n" +
                    "                    <tr>\n" +
                    "                        <td style=\"padding: 30px 50px; background-color: #f7f9fc; border-bottom-right-radius: 16px; border-bottom-left-radius: 16px;\">\n" +
                    "                            <p style=\"margin: 0; font-size: 13px; color: #80868b; line-height: 1.7;\">\n" +
                    "                                * 본 이메일은 포티카드 가입 신청에 따라 발송되었습니다.<br/>\n" +
                    "                                * 본인이 신청하지 않으셨다면 이 메일을 무시해 주세요.<br/>\n" +
                    "                                * 본 메일은 발신 전용으로 회신이 불가능합니다.<br/>\n" +
                    "                                <br/>\n" +
                    "                                <strong>© PORTI CARD. All rights reserved.</strong>\n" +
                    "                            </p>\n" +
                    "                        </td>\n" +
                    "                    </tr>\n" +
                    "\n" +
                    "                </table>\n" +
                    "            </td>\n" +
                    "        </tr>\n" +
                    "    </table>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setSubject(subject);

            helper.setText(htmlContents, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

