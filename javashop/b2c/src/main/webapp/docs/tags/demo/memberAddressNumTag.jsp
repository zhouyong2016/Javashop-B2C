
<!--创建"结算页会员地址数量"标签-->
<#assign memberAddressNum = newTag("memberAddressNum")>
<!--调用“结算页会员地址数量”标签-->
<#assign memberAddress = memberAddressNum()>

	收货人姓名：${memberAddress}</br>

