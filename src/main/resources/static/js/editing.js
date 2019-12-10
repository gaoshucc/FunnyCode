layui.use(['layer'], function () {
    let layer = layui.layer;

    getNoteType();
    $("#noteType").mousedown(getNoteType);
    showFunc();

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
                    let hiddenTypeId = document.querySelector("#hiddenTypeId");
                    select.innerHTML = "<option style='display: none'>请选择</option>";
                    let types = JSON.parse(data.data.types);
                    for(let i=0; i<types.length; i++){
                        select.appendChild(new Option(types[i].typeName, types[i].typeId));
                    }
                    select.options[hiddenTypeId.value].selected = true;
                }
            }
        });
    }

    function showFunc() {
        let status = document.querySelector("#status").value;
        let funcBar = document.querySelector("#funcBar");
        if(status === '2'){
            funcBar.innerHTML = funcBar.innerHTML +
                "<a class='func' id='publish' title='发布手记'>发布手记</a>" +
                "<a class='func' id='save' title='保存手记，下次继续编写'>保存手记</a>" +
                "<a class='func' id='delete' href='/user/mynotes' title='取消本次编辑，且所有修改不保存，该操作不可恢复'>取消</a>";
            document.querySelector("#save").addEventListener("click", save);
            document.querySelector("#publish").addEventListener("click", publish);
        }else if(status === '1'){
            funcBar.innerHTML = funcBar.innerHTML +
                "<a class='func' id='save' title='保存更改'>保存手记</a>" +
                "<a class='func' id='delete' href='/user/mynotes' title='取消本次编辑，且所有修改不保存，该操作不可恢复'>取消</a>";
            document.querySelector("#save").addEventListener("click", save);
        }
    }

    /**
     * 保存手记
     */
    let isClickSave = true;
    function save() {
        if(isClickSave){
            isClickSave = false;
            setTimeout(function () {
                isClickSave = true;
            }, 1000);
            $.ajax({
                type: "POST",
                dataType: "json",
                url: "/user/note/edit/save" ,
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
    let isClckPublish = true;
    function publish() {
        if(isClckPublish){
            isClckPublish = false;
            setTimeout(function () {
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: "/user/note/edit/publish" ,
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
            }, 1000);
        }
    }
});