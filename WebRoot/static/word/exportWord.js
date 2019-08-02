if (typeof jQuery !== "undefined" && typeof saveAs !== "undefined") {
    (function($) {
        $.fn.wordExport = function(fileName,) {
            fileName = typeof fileName !== 'undefined' ? fileName : "jQuery-Word-Export";
            var static = {
                mhtml: {
                    top: "Mime-Version: 1.0\nContent-Base: " + location.href + "\nContent-Type: Multipart/related; boundary=\"NEXT.ITEM-BOUNDARY\";type=\"text/html\"\n\n--NEXT.ITEM-BOUNDARY\nContent-Type: text/html; charset=\"utf-8\"\nContent-Location: " + location.href + "\n\n<!DOCTYPE html>\n<html>\n_html_</html>",
                    head: "<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n<style>\n_styles_\n</style>\n</head>\n",
                    body: "<body>_body_</body>"
                }
            };
            var options = {
                maxWidth: 624
            };
            // Clone selected element before manipulating it
            var markup = $(this).clone();

            // Remove hidden elements from the output
            markup.each(function() {
                var self = $(this);
                if (self.is(':hidden'))
                    self.remove();
            });

            // Embed all images using Data URLs
            var images = Array();
            var img = markup.find('img');
            for (var i = 0; i < img.length; i++) {
                // Calculate dimensions of output image
                var w = Math.min(img[i].width, options.maxWidth);
                var h = img[i].height * (w / img[i].width);
                // Create canvas for converting image to data URL
                var canvas = document.createElement("CANVAS");
                canvas.width = w;
                canvas.height = h;
                // Draw image to canvas
                var context = canvas.getContext('2d');
                context.drawImage(img[i], 0, 0, w, h);
                // Get data URL encoding of image
                var uri = canvas.toDataURL("image/png");
                $(img[i]).attr("src", img[i].src);
                img[i].width = w;
                img[i].height = h;
                // Save encoded image to array
                images[i] = {
                    type: uri.substring(uri.indexOf(":") + 1, uri.indexOf(";")),
                    encoding: uri.substring(uri.indexOf(";") + 1, uri.indexOf(",")),
                    location: $(img[i]).attr("src"),
                    data: uri.substring(uri.indexOf(",") + 1)
                };
            }

            // Prepare bottom of mhtml file with image data
            var mhtmlBottom = "\n";
            for (var i = 0; i < images.length; i++) {
                mhtmlBottom += "--NEXT.ITEM-BOUNDARY\n";
                mhtmlBottom += "Content-Location: " + images[i].location + "\n";
                mhtmlBottom += "Content-Type: " + images[i].type + "\n";
                mhtmlBottom += "Content-Transfer-Encoding: " + images[i].encoding + "\n\n";
                mhtmlBottom += images[i].data + "\n\n";
            }
            mhtmlBottom += "--NEXT.ITEM-BOUNDARY--";

            //TODO: load css from included stylesheet
            var styles = "html,body{width: 100%; height: 100%; position: absolute; top: 0; left: 0;}  *{margin: 0; padding: 0; border: none; outline: none; font: normal normal normal 100%/1.8em \"microsoft yahei\"; color: #333;}  dl,dt,dd,ul,li,ol{list-style: none;}  a{text-decoration: none;}  a:hover{color: #1d72f0;}    html body .indentNone{text-indent: 0;}  .textBox{margin: 20px;}  .textBox dt *{font-size: 16px; font-weight: bold;}  .setColor{background-color: #ff0;}  .setColor dt,  .setColor dd{display: inline-block; background-color: #ff0; margin-right: auto;}  .setColor dl{text-indent: 0;}  .setColor dl dt,  dd{text-indent: 28px;}  dd dd{margin-left: 28px;}    .titleBox{font-size: 14px;}  .titleBox h1{font-size: 24px; text-align: center;}  .titleCon p{text-indent: 28px;}    /*远程授课平台操作指南—学生端*/  .textBox .titleMax{font-size: 36px;}  .textBox h1 p{font-size: 18px;}  .navBox>.navOpenBtn,  .navBox>.navTopBtn{position: fixed; bottom: 100px; right: 20px; width: 50px; height: 50px; line-height: 50px; border-radius: 50%; color: #fff; background: #000; font-size: 13px; text-align: center; opacity: .4;}  .navBox>.navTopBtn{bottom: 40px;}  .navBox>.navFull,  .showImgBox,  .imgFull{display: none; position: fixed; top: 0; left: 0; bottom: 0; right: 0; background: #000; opacity: .3;}  .navBox>.nav{display: none; position: fixed; top: 20px; right: 20px; bottom: 50px; left: 20%; overflow: auto;}  .navBox>.nav>.navCon{float: right;}  .navBox>.nav>.navCon>li{background: #fff; border-bottom: 1px solid #ddd; padding: 3px 10px;}  .aBox{position: relative; white-space: nowrap;}  .navA{position: absolute; top: 0; left: 0; right: 0; bottom: 0;}    dd{/*overflow: hidden;*/}  .clearFlaot{display: block; clear: both;}  .imgBox{float: left; text-align: center; margin: 10px 20px 10px 0;}  .imgBox>img{display: block; width: 100%;}  .showImgBox{display: none; opacity: 1; z-index: 9; background: none;}  .imgFull{display: block; z-index: -1;}  .imgCon{display: block; width: 100%; height: 100%; overflow: auto;}  .imgCon img{width: 100%; height: auto;}  .navBox>.nav{display: inline-block; left: 0px; right: auto; overflow-x: hidden; padding-right: 20px; background:#fff; position: absolute; top:0; bottom:0;}  .navBox>.nav>.navCon{float: none;}  .navOpenBtn{display: none;}  .textBox dt *{font-weight: normal;}  /*设置每个层级的字号大小*/  dl dt{font-size: 22px;}  dl dl dt{font-size: 18px;}  dl dl dl dt{font-size: 16px;}  dl dl dl dl dt{font-size: 12px;}  dl dd.describe{font-size: 12px;}  dl dl dd.describe{font-size: 12px;}  dl dl dl dd.describe{font-size: 12px;}  dl dd{font-size: 16px;}  dl dl dd{font-size: 14px;}  dl dl dl dd{font-size: 12px;}  .navCon dl dt,.navCon dd{text-indent: 0; position: relative;}  .setColor dl dt,dd{text-indent: 0;}  dd{margin-left: 28px;}  .textBox dl{margin-bottom: 10px;}  .describe dd{text-indent: 0; margin-left: 63px; font-size: 12px;}  .textBox .processCon dd{text-indent: 0; margin-left: 63px;}  .describe dt{float: left; font-size: 12px;}  .textBox .processCon dt{float: left;}  .textBox .describe dt:after,  .textBox .processCon dt:after{content: '：';}  .textBox {position: absolute; right: 0; top: 0; bottom: 0; margin: 0 0 0 auto; overflow: auto;}  ";

            // Aggregate parts of the file together
            var fileContent = static.mhtml.top.replace("_html_", static.mhtml.head.replace("_styles_", styles) + static.mhtml.body.replace("_body_", markup.html())) + mhtmlBottom;

            // Create a Blob with the file contents
            var blob = new Blob([fileContent], {
                type: "application/msword;charset=utf-8"
            });
            //上传数据 
            var formData = new FormData();
            formData.append("data", blob);
            $.ajax({ url: 'FileDownload?fileName='+fileName + ".doc", 
            	type: 'post',
            	processData: false,
            	contentType: false, 
            	data: formData, 
            	dataType: 'json',
            	success: function (data) {
            	
            	}, error: function (jqXHR, textStatus, errorThrown) {
            		alert(textStatus + "---" + errorThrown); 
            	} });
            saveAs(blob, fileName + ".doc");
        };
    })(jQuery);
} else {
    if (typeof jQuery === "undefined") {
        console.error("jQuery Word Export: missing dependency (jQuery)");
    }
    if (typeof saveAs === "undefined") {
        console.error("jQuery Word Export: missing dependency (FileSaver.js)");
    }
}
