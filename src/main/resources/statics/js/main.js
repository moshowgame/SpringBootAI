
$.inputArea = undefined;
$.outputArea = undefined;

$(function(){
	//powered by zhengkai.blog.csdn.net

	//init input code area
	// $.inputArea = CodeMirror.fromTextArea(document.getElementById("inputArea"), {
	// 	mode: "text/plain", // SQL
	// 	theme: "idea",  // IDEA主题
	// 	lineNumbers: true,   //显示行号
	// 	smartIndent: true, // 自动缩进
	// 	autoCloseBrackets: true// 自动补全括号
	// });
	// $.inputArea.setSize('auto','auto');

	// init output code area
	// $.outputArea = CodeMirror.fromTextArea(document.getElementById("outputArea"), {
	// 	theme: "idea",   // IDEA主题
	// 	lineNumbers: true,   //显示行号
	// 	smartIndent: true, // 自动缩进
	// 	autoCloseBrackets: true// 自动补全括号
	// });
	// $.outputArea.setSize('auto','auto');

});


const vm = new Vue({
	el: '#rrapp',
	data: {
		formData: {
			tableSql: "AI:xxxxxxxxxx<br>YOU:xxxxxxxx<br>",
			options: {
				dataType: "sql",
				question:"请输入你的问题",
				isUseImage:"true",
				engine: "spark"
			}
		},
		templates:[{}],
		historicalData:[],
		outputStr: "${(value.outputStr)!!}",
		outputJson: {}
	},
	methods: {
		//set the template for output 选择页面输出的模板类型
		// setOutputModel: function (event) {
		// 	const targetModel = event.target.innerText.trim();
		// 	console.log(targetModel);
		// 	vm.currentSelect = targetModel ;
		// 	if(vm.outputStr.length>30){
		// 		vm.outputStr=vm.outputJson[targetModel];
		// 		$.outputArea.setValue(vm.outputStr.trim());
		// 		//console.log(vm.outputStr);
		// 		$.outputArea.setSize('auto', 'auto');
		// 	}
		// },
		//switch HistoricalData
		// switchHistoricalData: function (event) {
		// 	const tableName = event.target.innerText.trim();
		// 	console.log(tableName);
		// 	if (window.sessionStorage){
		// 		const valueSession = sessionStorage.getItem(tableName);
		// 		vm.outputJson = JSON.parse(valueSession);
		// 		console.log(valueSession);
		// 		alert("切换历史记录成功:"+tableName);
		// 	}else{
		// 		alert("浏览器不支持sessionStorage");
		// 	}
		// 	vm.outputStr=vm.outputJson[vm.currentSelect].trim();
		// 	$.outputArea.setValue(vm.outputStr);
		// 	//console.log(vm.outputStr);
		// 	$.outputArea.setSize('auto', 'auto');
		// },
		// setHistoricalData : function (tableName){
		// 	//add new table only
		// 	if(vm.historicalData.indexOf(tableName)<0){
		// 		vm.historicalData.unshift(tableName);
		// 	}
		// 	//remove last record , if more than N
		// 	if(vm.historicalData.length>9){
		// 		vm.historicalData.splice(9,1);
		// 	}
		// 	//get and set to session data
		// 	const valueSession = sessionStorage.getItem(tableName);
		// 	//remove if exists
		// 	if(valueSession!==undefined && valueSession!=null){
		// 		sessionStorage.removeItem(tableName);
		// 	}
		// 	//set data to session
		// 	sessionStorage.setItem(tableName,JSON.stringify(vm.outputJson));
		// 	//console.log(vm.historicalData);
		// },
		//request with formData to generate the code 根据参数生成代码
		generate : function(){
			//get value from codemirror
			//vm.formData.tableSql=$.inputArea.getValue();
			axios.post(basePath+"/generate",vm.formData.options).then(function(res){
				if(res.code===500){
					error("生成失败");
					return;
				}
				// setAllCookie();
				// //console.log(res.outputJson);
				// vm.outputJson=res.outputJson;
				// // console.log(vm.outputJson["bootstrap-ui"]);
				// vm.outputStr=vm.outputJson[vm.currentSelect].trim();
				// //console.log(vm.outputJson["bootstrap-ui"]);
				// //console.log(vm.outputStr);
				// $.outputArea.setValue(vm.outputStr);
				// $.outputArea.setSize('auto', 'auto');
				// //add to historicalData
				// vm.setHistoricalData(res.outputJson.tableName);
				alert("生成成功");
			});
		},
		copy : function (){
			navigator.clipboard.writeText(vm.outputStr.trim()).then(r => {alert("已复制")});
		}
	},
	created: function () {
		//load all templates for selections 加载所有模板供选择
		// axios.post(basePath+"/template/all",{
		// 	id:1234
		// }).then(function(res){
		// 	//console.log(res.templates);
		// 	vm.templates = JSON.parse(res.templates);
		// 	// console.log(vm.templates);
		// });
	},
	updated: function () {
	}
});

/**
 * 将所有 需要 保留历史纪录的字段写入Cookie中
 */
function setAllCookie() {
	var arr = list_key_need_load();
	for (var str of arr){
		setOneCookie(str);
	}
}

function setOneCookie(key) {
	setCookie(key, vm.formData.options[key]);
}

/**
 * 将所有 历史纪录 重加载回页面
 */
function loadAllCookie() {
	//console.log(vm);
	var arr = list_key_need_load();
	for (var str of arr){
		loadOneCookie(str);
	}
}

function loadOneCookie(key) {
	if (getCookie(key)!==""){
		vm.formData.options[key] = getCookie(key);
	}
}

