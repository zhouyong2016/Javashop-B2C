/*******************************************************************************
 * 进度条
 * 
 */
var Progress = function(config) {

	this.config = config || {};
	this.url = config.url;

	this.onFinished = config.onFinished;

};

Progress.fn = Progress.prototype = {
	constructor : Progress,
	show : function() {
		var self = this;
		$("#progress-wrap").html('<div class="pro" style="width:500px"></div><div class="taskmsg"/>');

		$("#progress-wrap").everyTime(1000, 'progress', function() {
			self.refresh();
		});

	},
	refresh : function() {
		var statusurl = this.url;
		var onFinished = this.onFinished;
		$.ajax({
			url : statusurl,
			dataType : "json",
			success : function(data) {
				if (data.result == 1) {
					var sum_per = data.data.sum_per;
					$('#progress-wrap>div.taskmsg').text(
							data.data.text + "(" + sum_per + "%)");
					element.progress('demo', sum_per + "%");
					if (data.data.task_status == 1) {
						$('#progress-wrap>div.taskmsg').text("任务已完成");
						$("#progress-wrap").stopTime('progress');
						if (typeof onFinished == 'function') {
							onFinished('success');
						}
						if (data.data.task_status == 2) {
							$('#progress-wrap>div.taskmsg').text(
									"任务出错：" + data.data.text);

							$("#progress-wrap").stopTime('progress');
							if (typeof onFinished == 'function') {
								onFinished('error');
							}
						}
					}
				} else {
					$("#progress-wrap").stopTime('progress');
					alert(data.message);
				}
			},
			error : function() {
				$("#progress-wrap").stopTime('progress');
				if (typeof onFinished == 'function') {
					onFinished('error');
				}
			}
		});
	}
};
