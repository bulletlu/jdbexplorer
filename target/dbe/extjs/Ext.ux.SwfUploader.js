/**
 * 文件上传组件；
 */
Ext.ux.SwfUploader = function() {
	/**
	 * 属性申明
	 */
	var flashPath = Ext.SWFUPLOADER_FLASH_URL
			|| '../swfupload/swfupload_f9.swf';
	var swfu = null;// SwfUploader控件
	var ready = false;// 是否初始化就绪
	var single = true;// 是否单文件上传
	var uploadEventHandle = null;// 上传事件处理对象
	var callBackObject = {};// 上传处理结果回调对象

	/**
	 * 数据处理对象声明
	 */
	// 单文件上传处理对象
	var createSingleFileUploadEventHandle = function(swfUploader) {
		// 进度栏
		var progressBar = null;

		return {
			addToQueued : function(file) {
				// 准备相关参数
				swfUploader.addFileParam(file.id, 'name', file.name);
				swfUploader.addFileParam(file.id, 'type', file.type);
				swfUploader.addFileParam(file.id, 'size', file.size);

				// alert("准备开始文件上传：" + file.id);
				swfUploader.startUpload(file.id);
			},
			startUpload : function(file) {
				// 显示上传进度条..
				var msg = "[" + file.name + "] Uploading...";
				progressBar = Ext.Msg.show({
					width : 320,
					title : '请稍等...',
					msg : msg,
					wait : true,
					// progress : true,
					// progressText : '1/100',
					// buttons : {
					// cancel : '取消'
					// },
					fn : function() {
						// alert('取消文件上传~~');
						swfUploader.cancelUpload(file.id);
						swfUploader.stopUpload();
					}
				});
			},
			uploadingProgress : function(file, complete, total) {
				// 进度处理过程..
			},
			uploadError : function(file, code, msg) {
				progressBar.hide();
				if (callBackObject.uploadError) {
					callBackObject.uploadError(file, code, msg);
				} else {
					alert("[" + file.name + "]上传失败:" + msg);
				}
			},
			uploadComplete : function(file) {
				progressBar.hide();
				if (callBackObject.uploadComplete) {
					callBackObject.uploadComplete(file);
				}
			},
			uploadSuccess : function(file, data) {
				if (callBackObject.uploadSuccess) {
					callBackObject.uploadSuccess(file, data);
				}
			}
		}
	};

	// 多文件上传处理对象
	var createMultiFileUploadEventHandle = function(swfUpload) {
		return {
			ready : false,
			selectFileBefore : false,
			selectFileAfter : false,
			addToQueued : false,
			addToQueuedError : false,
			startUpload : false,
			uploadingProgress : false,
			uploadError : false,
			uploadSuccess : false,
			uploadComplete : false
		}
	}

	/**
	 * 事件句柄声明
	 */
	// Flash控件准备就绪
	var onSwfUploadReady = function() {
		ready = true;
		if (single) {
			uploadEventHandle = createSingleFileUploadEventHandle(swfu);
		} else {
			uploadEventHandle = createMultiFileUploadEventHandle(swfu);
		}
		if (uploadEventHandle.ready) {
			uploadEventHandle.ready.createDelegate(this)();
		}
	}
	// 选择文件前
	var onSelectFileBefore = function() {
		if (uploadEventHandle.selectFileBefore) {
			uploadEventHandle.selectFileBefore.createDelegate(this)();
		}
	}
	// 选择文件后
	var onSelectFileAfter = function(selectIdx, queuedIdx) {
		if (selectIdx > 0) {
			if (uploadEventHandle.selectFileAfter) {
				uploadEventHandle.selectFileAfter.createDelegate(this)();
			}
		}
	}
	// 添加文件到上传队列
	var onAddFileToUploadQueued = function(file) {
		if (uploadEventHandle.addToQueued) {
			uploadEventHandle.addToQueued.createDelegate(this)(file);
		}
	}
	// 添加文件到上传队列时出错
	var onAddFileToUploadQueuedError = function(file, code, msg) {
		if (code == SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED) {
			msg = "文件上传队列限制已满~!";
		} else if (code == SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT) {
			msg = "文件大小超过限制~!";
		} else if (code == SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE) {
			msg = "文件大小为0字节~!";
		} else {
			msg = "文件类型无效~!";
		}
		if (uploadEventHandle.addToQueuedError) {
			uploadEventHandle.addToQueuedError.createDelegate(this)();
		} else {
			alert("操作失败：" + msg);
		}
	}
	// 开始文件上传任务
	var onStartFileUpload = function(file) {
		if (uploadEventHandle.startUpload) {
			uploadEventHandle.startUpload.createDelegate(this)(file);
		}
	}
	// 文件上传进度
	var onFileUploadProgress = function(file, complete, total) {
		if (uploadEventHandle.uploadingProgress) {
			uploadEventHandle.uploadingProgress.createDelegate(this)(file,
					complete, total);
		}
	}
	// 文件上传过程中 发生错误
	var onFileUploadingError = function(file, code, msg) {
		if (code = SWFUpload.UPLOAD_ERROR.HTTP_ERROR) {
			msg = 'HTTP_ERROR ' + msg;
		} else if (code = SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL) {
			msg = '未设置目标URL~!';
		} else if (code = SWFUpload.UPLOAD_ERROR.IO_ERROR) {
			msg = 'IO_ERROR';
		} else if (code = SWFUpload.UPLOAD_ERROR.SECURITY_ERROR) {
			msg = 'SECURITY_ERROR';
		} else if (code = SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED) {
			msg = 'UPLOAD_LIMIT_EXCEEDED';
		} else if (code = SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED) {
			msg = 'UPLOAD_FAILED';
		} else if (code = SWFUpload.UPLOAD_ERROR.SPECIFIED_FILE_ID_NOT_FOUND) {
			msg = 'SPECIFIED_FILE_ID_NOT_FOUND';
		} else if (code = SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED) {
			msg = 'FILE_VALIDATION_FAILED';
		} else if (code = SWFUpload.UPLOAD_ERROR.FILE_CANCELLED) {
			msg = '被取消~~!';
		} else if (code = SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
			msg = '被停止~~!';
		}

		if (uploadEventHandle.uploadError) {
			uploadEventHandle.uploadError.createDelegate(this)(file, code, msg);
		} else {
			alert("上传[" + file.name + "]失败：" + msg);
		}
	}
	// 文件上传成功
	var onFileUploadSuccess = function(file, data) {
		if (uploadEventHandle.uploadSuccess) {
			uploadEventHandle.uploadSuccess.createDelegate(this)(file, data);
		}
	}
	// 文件上传任务完成
	var onFileUploadComplete = function(file) {
		if (uploadEventHandle.uploadComplete) {
			uploadEventHandle.uploadComplete.createDelegate(this)(file);
		}
	}

	// 取得上传控件
	var createSwfUploader = function(config, singleFileUpload, callback) {
		// 初始化控件
		initSwfUploader(config);
		callBackObject = config.callback || {};// 取得回调对象

		// 阻塞 ，等待控件初始化完成后调用 callback
		var mywait = function() {
			if (ready) {
				single = singleFileUpload;
				if (callback) {
					callback(swfu);
				}
			} else {
				mywait.defer(100);
			}
		}
		mywait.defer(200);
	};

	/**
	 * 私有方法申明
	 */
	// 初始化SwfUpload控件
	var initSwfUploader = function(config) {
		if (!swfu) {
			// init swfupload
			if (config && config.file_types) {
				var type = config.file_types.type;
				var desc = config.file_types.desc;
				config.file_types = type;
				config.file_types_description = desc;
			}
			var settings = {
				flash_url : flashPath,
				file_post_name : 'file',
				swfupload_loaded_handler : onSwfUploadReady,
				file_dialog_start_handler : onSelectFileBefore,
				file_dialog_complete_handler : onSelectFileAfter,
				file_queued_handler : onAddFileToUploadQueued,
				file_queue_error_handler : onAddFileToUploadQueuedError,
				upload_start_handler : onStartFileUpload,
				upload_progress_handler : onFileUploadProgress,
				upload_error_handler : onFileUploadingError,
				upload_success_handler : onFileUploadSuccess,
				upload_complete_handler : onFileUploadComplete
			};
			config = Ext.applyIf(config || {}, settings);

			// create
			swfu = new SWFUpload(config);
		} else {
			if (config.upload_url) {
				// 上传目标URL
				swfu.setUploadURL(config.upload_url);
			}
			if (config.file_post_name) {
				// 上传文件字段名称
				swfu.setFilePostName(config.file_post_name);
			}
			if (config.post_params) {
				// 设置提交参数
				swfu.setPostParams(config.post_params);
			}
			if (config.file_types) {
				// 设置允许文件类型
				var type = config.file_types.type || "*.*";
				var desc = config.file_types.desc || "所有文件 (*.*)";
				if (desc != "所有文件 (*.*)") {
					desc = desc + " (" + type + ")";
				}
				swfu.setFileTypes(type, desc);
			}
			if (config.file_size_limit) {
				// 上传文件大小限制
				swfu.setFileSizeLimit(config.file_size_limit);
			}
			//重新配置事件句柄
			if (config.upload_success_handler) {
				swfu.uploadSuccess = config.upload_success_handler;
			}else{
				swfu.uploadSuccess = onFileUploadSuccess;
			}
		}
	}

	/**
	 * 返回上传控件对象
	 */
	return {
		/**
		 * 获取Uploader对象
		 */
		upload : function(config) {
			// 检查配置参数
			if (!config || !config.upload_url) {
				alert("未指定上传目标URL~~!");
				return;
			}
			// 创建文件 并直接调用文件上传..
			createSwfUploader(config, true, function(uploader) {
				uploader.selectFile();
			});
		}
	}
}();