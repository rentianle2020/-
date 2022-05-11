# Reservation(中频，难度高)

- Restaurant，Hodel

考虑可以预定的东西

Search criteria(有条件搜索) -> Search() -> List<Result>(返回可选的预定) -> Select() -> Receipt(可以根据小票获得服务或者取消预定)
Result要看实际情况，如果是酒店预定，应该返回不同的RoomType；如果是饭店预定，那么可以直接进入confirm阶段。



**RestaurantSystem**

Clarify

- What：Table（不同的桌子规格，包间）
- How（Feature）：Reservation，Delivery

![image-20220509220930930](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20220509220931.png)



### HotelReservationSystem

search -> select -> cancel

use LRU Cache for search

![image-20220509221028024](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20220509221028.png)