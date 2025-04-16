package com.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReminderController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 取得指定會員的離線提醒
     * @param memId 會員 ID
     * @return 該會員的提醒訊息列表（撈完會清空）
     */
    @GetMapping("/reminder/offline/{memId}")
    @ResponseBody
    public List<String> getOfflineReminders(@PathVariable Integer memId) {
        String key = "reminder:" + memId;
        List<String> reminders = redisTemplate.opsForList().range(key, 0, -1);
        redisTemplate.delete(key); // 撈完清空
        return reminders;
    }
}
