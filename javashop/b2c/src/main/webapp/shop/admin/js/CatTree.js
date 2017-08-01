var CatTree={
	init:function(){
		var self = this;
		$.ajax(
			{
				url:"/shop/api/cat!treejson.do?ajax=yes",
				success:function(catTree){
					
				}
			}	
		);
		
	},
	createTree:function(){
		
	}

};