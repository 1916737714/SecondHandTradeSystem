package com.example.andoirdsecondhandtradingsystem.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response


class AuthViewModel : ViewModel() {


   val apiService: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }
    private val _messageRecords = MutableLiveData<List<Data.MessageRecord>>()
    val messageRecords: LiveData<List<Data.MessageRecord>> get() = _messageRecords

    private var fromUserId: Int? = null
    private var userId: Int? = null
    private var currentpage: Int? = null

    /**
     * 注册用户
     */
    fun registerUser(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting network request")

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.registerUser(RegisterRequest(username, password)).execute()
                }

                Log.d("AuthViewModel", "Network request completed")

                if (response.isSuccessful) {
                    val registerresponse = response.body()
                    if (registerresponse != null && registerresponse.code == 200) {

                        Log.d("AuthViewModel", "Registration succeeded")
                        onSuccess()
                    } else {
                        Log.d("AuthViewModel", "Registration failed with message: ${registerresponse?.msg}")
                        onError(registerresponse?.msg ?: "注册失败")
                    }
                } else {
                    Log.d("AuthViewModel", "Response was not successful")
                    onError("注册失败")
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "HTTP exception", e)
                onError("请求失败: ${e.message}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "General exception", e)
                onError("请求失败: ${e.message}")
            }
        }
    }

    /**
     * 登录用户
     */
    fun loginUser(
        username: String,
        password: String,
        onSuccess: (Data.User) -> Unit, // 传递用户信息
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting login request for user: $username")
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.loginUser(LoginRequest(username, password)).execute()
                }

                if (response.isSuccessful) {
                    Log.d("getLoginMessage", "API response: $response")
                    //获取登录响应体
                    val loginresponse = response.body()
                    Log.d("getLoginMessagebody", "API response: $loginresponse")

                    if (loginresponse != null) {
                        Log.d("AuthViewModel", "API response code: ${loginresponse.code}")
                        Log.d("AuthViewModel", "API response message: ${loginresponse.msg}")
                        Log.d("AuthViewModel", "API response data: ${loginresponse.data}")

                        if (loginresponse.code == 200) {
                            val user = loginresponse.data as Data.User?

                            // 设置默认头像和密码
                            if (user != null) {

                                user.password = password
                            }

                            if (user != null) {
                                Log.d("AuthViewModel", "Login successful with user: $user")
                                onSuccess(user) // 传递用户信息
                            } else {
                                Log.e("AuthViewModel", "Login failed: data is null")
                                onError("登录失败: 用户数据为空")

                            }
                        } else {
                            Log.e("AuthViewModel", "Login failed: ${loginresponse.msg}")
                            onError(loginresponse.msg ?: "登录失败")
                        }
                    } else {
                        Log.e("AuthViewModel", "Login failed: response body is null")
                        onError("登录失败: 响应体为空")
                    }
                } else {
                    Log.e("AuthViewModel", "Login failed with response code: ${response.code()}")
                    onError("登录失败")
                }
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "HTTP exception during login", e)
                onError("请求失败: ${e.message()}")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                onError("请求失败: ${e.message}")
            }
        }
    }

    /**
     * 获取消息列表
     */
    fun getMessageList(
        userId: Int,
        onSuccess: (List<Data.MessageListData>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("getMessageList", "Requesting messages for userId: $userId")

                // 每次请求都创建新的 Call 实例
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getMessageList(userId).execute()
                }

                Log.d("getMessageList", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val messageListResponse = response.body()
                    Log.d("getMessageList", "MessageListResponse: $messageListResponse")

                    if (messageListResponse != null && messageListResponse.code == 200) {
                        val data = messageListResponse.data
                        if (data is Data.DataWrapper) {
                            Log.d("成功", "Message list request succeeded ${data}")
                            onSuccess(data.messageList)
                        } else {
                            onError("Unexpected data type")
                        }
                    } else {
                        onError(messageListResponse?.msg ?: "请求失败")
                    }
                } else {
                    onError("请求失败: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("请求http失败: ${e.message}")
            } catch (e: Exception) {
                onError("请求异常，导致失败: ${e.message}")
            }
        }
    }

    /**
     * 获取消息详情
     */
    fun getMessageDetail(
        fromUserId: Int,
        userId: Int,
        page: Int,
        onSuccess: (List<Data.MessageRecord>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("getMessageDetail", "Requesting getMessageDetail for fromUserId: $fromUserId, userId: $userId, page: $page")

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getMessageDetail(fromUserId, userId, page).execute()
                }

                Log.d("getMessageDetail", "Response: ${response.body()}")

                if (response.isSuccessful) {
                    val messageDetailResponse = response.body()
                    Log.d("getMessageDetail", "getMessageDetailResponse: $messageDetailResponse")

                    if (messageDetailResponse != null && messageDetailResponse.code == 200) {
                        val data = (messageDetailResponse.data as? Data.MessageDatail)?.records
                        if (data != null) {
                            Log.d("getMessageDetail", "MessageDetail request succeeded ${data}")
                            val currentList = _messageRecords.value ?: emptyList()
                            _messageRecords.postValue(if (page == 1) data else currentList + data) // 更新 LiveData
                            onSuccess(data)
                        } else {
                            onError("No data available")
                        }
                    } else {
                        onError(messageDetailResponse?.msg ?: "Request failed")
                    }
                } else {
                    onError("getMessageDetail request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("getMessageDetail HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                onError("getMessageDetail request failed due to exception: ${e.message}")
            }
        }
    }


    /**
     * 发送消息
     */

    fun sendMessage(
        userId: Int,
        toUserId: Int,
        content: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.sendMessage(SendMessageRequest(userId, toUserId, content)).execute()
                }

                if (response.isSuccessful) {
                    val sendMessageDetailResponse = response.body()
                    if (sendMessageDetailResponse != null && sendMessageDetailResponse.code == 200) {
                        onSuccess()
                    } else {
                        onError(sendMessageDetailResponse?.msg ?: "Request failed")
                    }
                } else {
                    onError("SendMessageDetail request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("SendMessageDetail HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                onError("SendMessageDetail request failed due to exception: ${e.message}")
            }
        }
    }

    /**
     * 设置用户ID，开始轮询,更新消息
     */
    fun setUserIds(fromUserId: Int, userId: Int,currentpage:Int) {
        this.fromUserId = fromUserId
        this.userId = userId
        this.currentpage=currentpage
        startPolling() // 开始轮询
    }
    //获取服务器历史消息
    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(3500) // 每隔5秒轮询一次
                fromUserId?.let { fromId ->
                    userId?.let { userId ->
                        currentpage?.let { page ->
                            getMessageDetail(
                                fromUserId = fromId,
                                userId = userId,
                                page = page,
                                onSuccess = { data ->
                                    if (data.isEmpty()) {
                                        // 如果返回的数据为空，重置 currentpage 为 1
                                        currentpage = 1
                                    } else {
                                        // 如果返回的数据不为空，增加 currentpage
                                        currentpage = page + 1
                                        updateMessageRecords(data, append = true)
                                    }
                                },
                                onError = {
                                    // 处理错误情况
                                }
                            )
                        }

                    }
                }
            }
        }
    }
    //更新消息列表
    fun updateMessageRecords(newMessages: List<Data.MessageRecord>, append: Boolean = true) {
        _messageRecords.value = if (append) {
            (newMessages + _messageRecords.value.orEmpty()).distinctBy { it.id }
        } else {
            newMessages
        }
    }

    /**
     * 将消息修改为已读
     */

    fun MarkMessageRead(
        chatId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.markMessageAsRead(chatId).execute()
                }

                if (response.isSuccessful) {
                    val MarkMessageReadResponse = response.body()
                    if (MarkMessageReadResponse != null && MarkMessageReadResponse.code == 200) {
                        onSuccess()
                    } else {
                        onError(MarkMessageReadResponse?.msg ?: "Request failed")
                    }
                } else {
                    onError("MarkMessageRead request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                onError("MarkMessageRead HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                onError("MarkMessageRead request failed due to exception: ${e.message}")
            }
        }
    }
    /**
     * 获取全部商品类型
     */

    fun getAllGoodsType(
        onSuccess: (List<Data.goodsType>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getAllGoodsType().execute()
                }

                if (response.isSuccessful) {
                    val getAllGoodsTypeResponse = response.body()

                    if (getAllGoodsTypeResponse != null && getAllGoodsTypeResponse.code == 200) {

                        val goodsTypeList = (getAllGoodsTypeResponse.data as Data.goodsTypeList).GoodsTypeList
                       Log.d("getAllGoodsType","getAllGoodsType Sucussee${getAllGoodsTypeResponse.data}")
                        onSuccess(goodsTypeList)

                    } else {
                        Log.e("getAllGoodsType","getAllGoodsType Failed")
                    }
                } else {
                    Log.e("getAllGoodsType","getAllGoodsType request failed: ${response.code()}")
                    onError("getAllGoodsTypeException request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e ("getAllGoodsTypeFunction…","getAllGoodsType HTTP request failed: ${e.message}")
                onError("getAllGoodsType HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("getAllGoodsTypeException","getAllGoodsType request failed due to exception: ${e.message}")
                onError("getAllGoodsType request failed due to exception: ${e.message}")
            }
        }
    }

    /**
     * 上传图片
     */

    fun uploadFiles(
        files:  List<MultipartBody.Part>,
        onSuccess: (Data.upLoadFile) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.uploadFiles(files).execute()
                }

                if (response.isSuccessful) {
                    val uploadFilesResponse = response.body()

                    Log.d("uploadFiles","uploadFiles Sucussee${uploadFilesResponse}")

                    if (uploadFilesResponse != null && uploadFilesResponse.code == 200) {

                        val uploadFiles = uploadFilesResponse.data as Data.upLoadFile
                        Log.d("uploadFiles","uploadFiles Sucussee${uploadFilesResponse.data}")
                        onSuccess(uploadFiles)

                    } else {
                        Log.e("uploadFiles","uploadFiles Failed")
                    }
                } else {
                    Log.e("uploadFiles","uploadFiles request failed: ${response.code()}")
                    onError("uploadFiles request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e ("uploadFiles…","uploadFiles HTTP request failed: ${e.message}")
                onError("uploadFiles HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("uploadFiles","uploadFiles request failed due to exception: ${e.message}")
                onError("uploadFiles request failed due to exception: ${e.message}")
            }
        }
    }



    /**
     * 新增商品信息
     */
    fun addGoodsInfo(
         addr: String,
         content: String,
         imageCode: String,
         price: Int,
         typeId: Int,
         typeName: String,
         userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.addGoodsInfo(AddGoodsRequest(addr,content,imageCode,price,typeId,typeName,userId)).execute()
                }

                if (response.isSuccessful) {
                    val addGoodsInfoResponse = response.body()
                    Log.d("addGoodsInfo","addGoodsInfo ${addGoodsInfoResponse}")
                    if (addGoodsInfoResponse != null && addGoodsInfoResponse.code == 200) {
                        Log.d("addGoodsInfo","addGoodsInfo Sucussee${addGoodsInfoResponse.data}")
                        onSuccess()
                    } else {
                        Log.e("addGoodsInfo","addGoodsInfo Failed")
                    }
                } else {
                    Log.e("addGoodsInfo","addGoodsInfo request failed: ${response.code()}")
                    onError("addGoodsInfo request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e ("addGoodsInfo…","addGoodsInfo HTTP request failed: ${e.message}")
                onError("addGoodsInfo HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("addGoodsInfo","addGoodsInfo request failed due to exception: ${e.message}")
                onError("addGoodsInfo request failed due to exception: ${e.message}")
            }
        }
    }

    /**
     * 保存商品信息
     */

    fun saveGoodsInfo(
        addr: String,
        content: String,
        imageCode: String,
        price: Int,
        typeId: Int,
        typeName: String,
        userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.saveGoodsInfo(SaveGoodsRequest(addr,content,imageCode,price,typeId,typeName,userId)).execute()
                }

                if (response.isSuccessful) {
                    val saveGoodsInfoResponse = response.body()

                    if (saveGoodsInfoResponse != null && saveGoodsInfoResponse.code == 200) {
                        Log.d("saveGoodsInfo","saveGoodsInfo Sucussee${saveGoodsInfoResponse.data}")
                        onSuccess()
                    } else {
                        Log.e("saveGoodsInfo","saveGoodsInfo Failed")
                    }
                } else {
                    Log.e("saveGoodsInfo","saveGoodsInfo request failed: ${response.code()}")
                    onError("saveGoodsInfo request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e ("saveGoodsInfo…","saveGoodsInfo HTTP request failed: ${e.message}")
                onError("saveGoodsInfo HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("saveGoodsInfo","saveGoodsInfo request failed due to exception: ${e.message}")
                onError("saveGoodsInfo request failed due to exception: ${e.message}")
            }
        }
    }

    /**
     * 获取用户已保存的商品列表
     */

    fun getsaveGoodsList(
        userId: Int,
        current: Int,
        onSuccess: (List<Data.saveGoodsInfo>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val response: Response<ApiResponse> = withContext(Dispatchers.IO) {
                    apiService.getSavedGoodsList(userId,current).execute()
                }

                if (response.isSuccessful) {
                    val saveGoodsListResponse = response.body()

                    if (saveGoodsListResponse != null && saveGoodsListResponse.code == 200) {
                        val saveGoodsList = (saveGoodsListResponse.data as Data.saveGoodsList).records
                        Log.d("getsaveGoodsList","getsaveGoodsList Sucussee${saveGoodsListResponse.data}")
                        onSuccess(saveGoodsList)
                    } else {
                        Log.e("getsaveGoodsList","getsaveGoodsList Failed")
                    }
                } else {
                    Log.e("getsaveGoodsList","getsaveGoodsList request failed: ${response.code()}")
                    onError("getsaveGoodsList request failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e ("getsaveGoodsList…","getsaveGoodsList HTTP request failed: ${e.message}")
                onError("getsaveGoodsList HTTP request failed: ${e.message}")
            } catch (e: Exception) {
                Log.e("getsaveGoodsList","getsaveGoodsList request failed due to exception: ${e.message}")
                onError("getsaveGoodsList request failed due to exception: ${e.message}")
            }
        }
    }

}

