package cn.odboy;

import cn.hutool.core.bean.BeanUtil;
import cn.odboy.modules.system.domain.User;
import cn.odboy.modules.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SystemUserTests {
    @Autowired
    private UserService userService;

    @Test
    public void addUserTest() {
        User record = userService.findById(2);
        record.setId(null);
        record.setCreateBy(null);
        record.setCreateTime(null);
        record.setUpdateBy(null);
        record.setUpdateTime(null);

        List<User> addList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User base = BeanUtil.copyProperties(record, User.class);
            String tag = "test00" + (100 + i);
            base.setPhone("18797874" + tag);
            base.setEmail("194381508" + tag + "@qq.com");
            base.setUsername("test00" + tag);
            base.setNickName("Test00" + tag);
            addList.add(base);
        }
        userService.saveBatch(addList);
    }
}

