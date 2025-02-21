var fileArr;
var fileInfoArr = [];

//썸네일 클릭시 삭제.
function fileRemove(index) {
    console.log("index: " + index);
    fileInfoArr.splice(index, 1);

    var imgId = "#img_id_" + index;
    $(imgId).remove();
    console.log(fileInfoArr);
}

// 썸네일 미리보기.
function previewImage(targetObj, View_area) {
    var files = targetObj.files;
    var preview = document.getElementById(View_area); // div id
    var ua = window.navigator.userAgent;
    console.log("파일 유무:" + files)
    console.log("보이는곳 유무:" + preview)

    // IE일 때 (IE8 이하에서만 작동)
    if (ua.indexOf("MSIE") > -1) {
        targetObj.select();
        try {
            var src = document.selection.createRange().text; // get file full path (IE9, IE10에서 사용 불가)
            var ie_preview_error = document.getElementById("ie_preview_error_" + View_area);

            if (ie_preview_error) {
                preview.removeChild(ie_preview_error); // error가 있으면 delete
            }

            var img = document.getElementById(View_area); // 이미지가 뿌려질 곳

            // 이미지 로딩, sizingMethod는 div에 맞춰서 사이즈를 자동조절 하는 역할
            img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + src + "', sizingMethod='scale')";
        } catch (e) {
            if (!document.getElementById("ie_preview_error_" + View_area)) {
                var info = document.createElement("p");
                info.id = "ie_preview_error_" + View_area;
                info.innerHTML = e.name;
                preview.insertBefore(info, null);
            }
        }
    } else {
        // 다른 브라우저 (크롬, 사파리, FF)
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var imageType = /image.*/; // 이미지 파일일 경우만 뿌려준다.
            if (!file.type.match(imageType))
                continue;

            var span = document.createElement('span');
            span.id = "img_id_" + i;
            span.style.width = '100px';
            span.style.height = '100px';
            preview.appendChild(span);

            var img = document.createElement("img");
            //var div =document.createElement("div");

            //console.log("div 유무:" + div)
            img.className = "addImg";
            img.classList.add("obj");
            img.file = file;
            img.style.width = 'inherit';
            img.style.height = 'inherit';
            img.style.cursor = 'pointer';
            const idx = i;
            img.onclick = () => fileRemove(idx) //이미지를 클릭했을 때.

            span.appendChild(img);

            if (window.FileReader) { // FireFox, Chrome, Opera 확인.
                var reader = new FileReader();
                reader.onloadend = (function (aImg) {
                    return function (e) {
                        aImg.src = e.target.result;
                    };
                })(img);
                reader.readAsDataURL(file);
            } else { // Safari is not supported FileReader
                if (!document.getElementById("sfr_preview_error_" + View_area)) {
                    var info = document.createElement("p");
                    info.id = "sfr_preview_error_" + View_area;
                    info.innerHTML = "Not supported FileReader";
                    preview.insertBefore(info, null);
                }
            }
        }
    }
}