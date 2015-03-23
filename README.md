# bike
  bike

### 功能介绍

  - 总里程
  - 单次里程
  - 车速 (km/h)

### 设置选项

  - 清空单次里程
  - 车轮周长(mm)
  - 检测磁铁个数(mm)

### 设备 API
  描述                      命令ID      命令    

  - 清空单次里程              100         $B> 100
  - 设置车轮周长              101         $B> 101 (uint16 车轮周长)      
  - 设置磁铁个数              102         $B> 102 (uint8 磁铁数量)      

  - 获取版本号                200         $B< 200
  - 获取实时速度              201         $B< 201
  - 获取单次里程              202         $B< 202
  - 获取全部里程              203         $B< 203
   

