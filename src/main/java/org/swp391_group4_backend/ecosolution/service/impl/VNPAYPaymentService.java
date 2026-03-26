package org.swp391_group4_backend.ecosolution.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.config.VNPayConfig;
import org.swp391_group4_backend.ecosolution.service.PaymentService;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPAYPaymentService implements PaymentService {

    private final VNPayConfig vnPayConfig;

    public VNPAYPaymentService(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    public String createPaymentUrl(BigDecimal amount, String orderInfo,String vnp_TxnRef, HttpServletRequest request) {
        // 1. Các tham số cơ bản
        String vnp_Version = vnPayConfig.vnp_Version;
        String vnp_Command = vnPayConfig.vnp_Command;
        //String vnp_TxnRef = vnPayConfig.getRandomNumber(8); // Mã giao dịch
        String vnp_IpAddr = "127.0.0.1"; // Trong thực tế lấy từ request.getRemoteAddr()
        String vnp_TmnCode = vnPayConfig.vnp_TmnCode;
        long amountInVnPayFormat = amount.multiply(new BigDecimal(100)).longValue(); // VNPay yêu cầu số tiền nhân 100
        // 2. Đưa tham số vào Map (VNPay yêu cầu phải sắp xếp theo bảng chữ cái)
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVnPayFormat)); // VNPay quy định số tiền nhân 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // 3. Xử lý thời gian tạo và hết hạn giao dịch (15 phút)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // 4. Sắp xếp Map và build chuỗi URL + Hash data
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {

                    // ĐÃ SỬA THÀNH UTF_8
                    String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");

                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(encodedValue);

                    // Build query (ĐÃ SỬA THÀNH UTF_8)
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(encodedValue);

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error encoding URL params", e);
        }

        // 5. Ký chuỗi bằng mã bí mật
        String queryUrl = query.toString();

        // Ép cắt bỏ khoảng trắng thừa bằng .trim() để bảo vệ an toàn
        String cleanSecretKey = vnPayConfig.secretKey.trim();

        // Đọc từ cấu hình, không hardcode nữa
        String vnp_SecureHash = vnPayConfig.hmacSHA512(cleanSecretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        // Trả về URL cuối cùng
        return vnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    // Thêm hàm này vào dưới hàm createPaymentUrl trong PaymentService
    public boolean verifyIpnSignature(Map<String, String> params) {
        String reqSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_SecureHash");

        // Sắp xếp lại các tham số y hệt như lúc tạo link
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    // ĐÃ SỬA THÀNH UTF_8
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            // Băm lại data bằng Secret Key của mình
            String mySecureHash = vnPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());

            // So sánh Hash tự tính và Hash do VNPay gửi
            return mySecureHash.equals(reqSecureHash);

        } catch (Exception e) {
            return false;
        }
    }

}
