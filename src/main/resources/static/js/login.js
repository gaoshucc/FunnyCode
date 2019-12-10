layui.use(['layer', 'element'], function () {
    let layer = layui.layer,
        element = layui.element;

    getValicodeImg();
    form_validate();

    let qqLogin = document.querySelector("#qq-login"),
        isClickQQLogin = true;
    qqLogin.addEventListener("click", function () {
        if(isClickQQLogin){
            isClickQQLogin = false;
            setTimeout(function () {
                isClickQQLogin = true;
            }, 1000);
            $.ajax({
                type: "GET",
                dataType: "json",
                url: "/qq/url" ,
                success : function(data){
                    if(data.code === REQ_SUCC){
                        let url = data.data.url;
                        openwindow(
                            url,
                            "_self",
                            550,
                            350
                        );
                    }else{
                        layer.msg(data.code + ":" + data.msg, {
                            time: 2500
                        });
                    }
                }
            });
        }
    });

    //获取验证码
    function getValicodeImg() {
        let valicodeImg = document.querySelector("#vali-code-img");
        valicodeImg.src = PRO_NAME + "/valicode?" + new Date();
        valicodeImg.addEventListener("click", function (e) {
            valicodeImg.src = PRO_NAME + "/valicode?" + new Date();
        });
    }
    //校验表单
    function form_validate() {
        let phoneExp = /^0?(13[0-9]|15[012356789]|17[013678]|18[0-9]|14[57])[0-9]{8}$/;
        jQuery.validator.addMethod("phone", function(value, element) {
            return this.optional(element) || phoneExp.test(value);
        }, "账号需为正常可用的11位手机号");

        $("#login-form").validate({
            rules: {
                username: {
                    required: true,
                    rangelength: [11, 11],
                    phone: true,
                    remote: {
                        url: PRO_NAME + "/usernameexists",
                        type: "post",
                        dataType: "json",
                        data: {
                            username: function () {
                                return $("#login-username").val();
                            }
                        }
                    }
                },
                password: {
                    required: true,
                    minlength: 8
                }
            },
            messages: {
                username: {
                    required: "账号不能为空",
                    rangelength: "请输入正确的手机号",
                    remote: "用户不存在"
                },
                password: {
                    required: "密码不能为空",
                    minlength: "密码长度必须大于8个字符"
                }
            }
        });

        let isClickRegBtn = true;
        $("#regist-form").validate({
            rules: {
                username: {
                    required: true,
                    rangelength: [11, 11],
                    phone: true,
                    remote: {
                        url: PRO_NAME + "/userexists",
                        type: "post",
                        dataType: "json",
                        data: {
                            username: function () {
                                return $("#username").val();
                            }
                        }
                    }
                },
                nickname: {
                    required: true,
                    minlength: 4,
                    maxlength: 20,
                    remote: {
                        url: PRO_NAME + "/nicknameexists",
                        type: "post",
                        dataType: "json",
                        data: {
                            nickname: function () {
                                return $("#nickname").val();
                            }
                        }
                    }
                },
                password: {
                    required: true,
                    minlength: 8
                },
                valicode: {
                    required: true,
                    remote: {
                        url: PRO_NAME + "/checkValicode",
                        type: "post",
                        dataType: "json",
                        data: {
                            valicode: function () {
                                return $("#vali-code").val();
                            }
                        }
                    }
                }
            },
            messages: {
                username: {
                    required: "账号不能为空",
                    rangelength: "请输入正确的手机号",
                    remote: "用户已存在，请直接登录"
                },
                nickname: {
                    required: "昵称不能为空",
                    minlength: "昵称长度不能少于4",
                    maxlength: "昵称长度不能超过20",
                    remote: "昵称已存在"
                },
                password: {
                    required: "密码不能为空",
                    minlength: "密码长度必须大于8个字符"
                },
                valicode: {
                    required: "验证码错误",
                    remote: "验证码错误"
                }
            },
            onsubmit:true,// 是否在提交时验证
            submitHandler:function(form){
                if(isClickRegBtn){
                    isClickRegBtn = false;
                    setTimeout(function () {
                        isClickRegBtn = true;
                    }, 1500);
                    $.ajax({
                        type: "POST",
                        dataType: "json",
                        url: "/reg" ,
                        data: $('#regist-form').serialize(),
                        success : function(data){
                            if(data.code === REQ_SUCC){
                                $('#popup').toggleClass('show');
                                layer.msg("注册成功，请登录", {
                                    time: 2500
                                });
                            }else{
                                layer.msg("注册失败", {
                                    time: 2500
                                });
                            }
                        }
                    });
                }
            }
        });
    }
});