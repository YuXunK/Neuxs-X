package org.yuxun.x.nexusx.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/api/uuid")
public class PCtoGetUUIDController {

    /**
     * @author yuxun
     * @description 用于获取PC设备对应的UUID作为唯一标识符存储到数据库
     */
    @GetMapping("/getDeviceUUID")
    public ResponseEntity<String> getDeviceUUID() {
        try {
            Process process = Runtime.getRuntime().exec("wmic csproduct get UUID");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder uuid = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.contains("UUID")) {
                    uuid.append(line.trim());
                }
            }

            process.waitFor();
            return ResponseEntity.ok(uuid.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve UUID");
        }
    }


}
