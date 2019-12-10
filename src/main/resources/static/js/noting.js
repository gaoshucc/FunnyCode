layui.use(['layer'], function () {
    let layer = layui.layer;

    getNoteType();
    $("#noteType").mousedown(getNoteType);
    document.querySelector("#publish").addEventListener("click", publish);
    document.querySelector("#save").addEventListener("click", save);

    /**
     * 获取手记类型
     */
    function getNoteType(){
        $.ajax({
            type: "GET",
            dataType: "json",
            url: "/user/note/type",
            success : function(data){
                let select = document.querySelector("#noteType");
                if(data.code === REQ_SUCC){
                    select.innerHTML = "<option style='display: none'>请选择</option>";
                    let types = JSON.parse(data.data.types);
                    for(let i=0; i<types.length; i++){
                        select.appendChild(new Option(types[i].typeName, types[i].typeId));
                    }
                }
            }
        });
    }

    /**
     * 保存手记
     */
    let isClickSaveBtn = true;
    function save() {
        if(isClickSaveBtn){
            isClickSaveBtn = false;
            setTimeout(function () {
                isClickSaveBtn = true;
            }, 1000);
            $.ajax({
                type: "POST",
                dataType: "json",
                url: "/user/note/save" ,
                data: $("#note-form").serialize(),
                success : function(data){
                    if(data.code === REQ_SUCC){
                        layer.msg("保存成功", {
                            time: 2500
                        },function () {
                            window.location.href = "/user/mynotes";
                        });
                    }else{
                        layer.msg("保存失败", {
                            time: 2500
                        });
                    }
                    printRetMsg(data.code, data.msg);
                }
            });
        }
    }
    /**
     * 发布手记
     */
    let isClickPublishBtn = true;
    function publish() {
        if(isClickPublishBtn){
            isClickPublishBtn = false;
            setTimeout(function () {
                isClickPublishBtn = true;
            }, 1000);
            $.ajax({
                type: "POST",
                dataType: "json",
                url: "/user/note/publish" ,
                data: $("#note-form").serialize(),
                success : function(data){
                    if(data.code === REQ_SUCC){
                        layer.msg("发布成功", {
                            time: 2500
                        },function () {
                            window.location.href = "/user/mynotes";
                        });
                    }else{
                        layer.msg("发布失败", {
                            time: 2500
                        });
                    }
                    printRetMsg(data.code, data.msg);
                }
            });
        }
    }

    function form_validate() {
        $("#note-form").validate({
            rules: {
                title: {
                    required: true
                },
                typeId: {
                    required: true
                },
                markdownDoc: {
                    required: true
                }
            },
            messages: {
                title: {
                    required: "标题不能为空"
                },
                typeId: {
                    required: "类型不能为空"
                },
                markdownDoc: {
                    required: "手记内容不能为空"
                }
            },

            onsubmit:true,// 是否在提交时验证

            submitHandler:function(form){

            }
        });
    }
})