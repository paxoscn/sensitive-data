/*
 * MIT License
 * 
 * Copyright (c) 2024 Mergen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.cn.common.data_crypt_demo.web;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.cn.common.data_crypt_demo.po.MySensitiveDataPO;
import dev.cn.common.data_crypt_demo.vo.Response;
import java.util.List;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class MySensitiveDataControllerTest {

    @Resource
    private MySensitiveDataController mySensitiveDataController;

    @BeforeAll
    public void setUp() {
        Response<List<MySensitiveDataPO>> response = mySensitiveDataController.list();

        for (MySensitiveDataPO po : response.getData()) {
            mySensitiveDataController.delete(po.getId());
        }
    }

    @Test
    public void testList() {
        MySensitiveDataPO po = new MySensitiveDataPO();
        po.setName("name");
        po.setTel("13800138000");
        mySensitiveDataController.create(po);

        Response<List<MySensitiveDataPO>> response = mySensitiveDataController.list();

        po = response.getData().get(0);

        assertEquals("name", po.getName());
        assertEquals("13800138000", po.getTel());
    }

    @Test
    public void testCreate() {
        MySensitiveDataPO po = new MySensitiveDataPO();
        po.setName("name");
        po.setTel("13800138000");
        Response<String> response = mySensitiveDataController.create(po);

        assertEquals("success", response.getData());
    }

    @Test
    public void testUpdate() {
        MySensitiveDataPO po = new MySensitiveDataPO();
        po.setName("name");
        po.setTel("13800138000");
        mySensitiveDataController.create(po);

        po = new MySensitiveDataPO();
        po.setName("name");
        po.setTel("13800138001");

        Response<String> response = mySensitiveDataController.update(mySensitiveDataController.list().getData().get(0).getId(), po);

        assertEquals("success", response.getData());
        assertEquals("13800138001", mySensitiveDataController.list().getData().get(0).getTel());
    }
} 