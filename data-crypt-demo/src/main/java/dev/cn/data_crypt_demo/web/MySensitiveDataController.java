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
package dev.cn.data_crypt_demo.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.cn.data_crypt_demo.mapper.MySensitiveDataMapper;
import dev.cn.data_crypt_demo.po.MySensitiveDataPO;
import dev.cn.data_crypt_demo.vo.Response;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
public class MySensitiveDataController {

    @Resource
    private MySensitiveDataMapper mySensitiveDataMapper;

    @GetMapping(value = "/my-sensitive-data")
    public Response<List<MySensitiveDataPO>> list() {
        return Response.ok(mySensitiveDataMapper.selectList(null));
    }

    @PutMapping(value = "/my-sensitive-data")
    public Response<String> create(@RequestBody MySensitiveDataPO po) {
        mySensitiveDataMapper.insert(po);
        return Response.ok("success");
    }

    @PostMapping(value = "/my-sensitive-data/{id}")
    public Response<String> update(@PathVariable Long id, @RequestBody MySensitiveDataPO po) {
        po.setId(id);
        mySensitiveDataMapper.updateById(po);
        return Response.ok("success");
    }

    @DeleteMapping(value = "/my-sensitive-data")
    public Response<String> delete(@RequestParam Long id) {
        mySensitiveDataMapper.deleteById(id);
        return Response.ok("success");
    }
}
