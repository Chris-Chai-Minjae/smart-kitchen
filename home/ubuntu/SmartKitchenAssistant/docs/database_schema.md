# 스마트 요리 보조 시스템 데이터베이스 스키마

## 개요

스마트 요리 보조 시스템은 Room 데이터베이스를 사용하여 로컬 데이터를 관리합니다. 데이터베이스는 레시피, 식재료, 식품 재고, 타이머, 식단 계획 등의 정보를 저장하고 관리합니다.

## 엔티티 설계

### 1. 레시피 관련 테이블

#### Recipe (레시피)
```
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 레시피 이름
    val description: String,               // 레시피 설명
    val prepTime: Int,                     // 준비 시간(분)
    val cookTime: Int,                     // 조리 시간(분)
    val servings: Int,                     // 인분 수
    val difficulty: String,                // 난이도(쉬움, 중간, 어려움)
    val imageUrl: String?,                 // 레시피 이미지 URL
    val isFavorite: Boolean = false,       // 즐겨찾기 여부
    val source: String?,                   // 레시피 출처
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
```

#### RecipeStep (레시피 단계)
```
@Entity(
    tableName = "recipe_steps",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class RecipeStep(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,                    // 레시피 ID
    val stepNumber: Int,                   // 단계 번호
    val description: String,               // 단계 설명
    val imageUrl: String?,                 // 단계 이미지 URL
    val videoUrl: String?,                 // 단계 비디오 URL
    val timerDuration: Int? = null         // 타이머 시간(초)
)
```

#### Ingredient (재료)
```
@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val id: Long = 0,
    val name: String,                      // 재료 이름
    val category: String,                  // 재료 카테고리
    val imageUrl: String?                  // 재료 이미지 URL
)
```

#### RecipeIngredient (레시피-재료 연결)
```
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("ingredientId")]
)
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,                    // 레시피 ID
    val ingredientId: Long,                // 재료 ID
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위(g, ml, 개 등)
    val isOptional: Boolean = false        // 선택 재료 여부
)
```

#### RecipeTag (레시피 태그)
```
@Entity(tableName = "recipe_tags")
data class RecipeTag(
    @PrimaryKey val id: Long = 0,
    val name: String                       // 태그 이름
)
```

#### RecipeTagCrossRef (레시피-태그 연결)
```
@Entity(
    tableName = "recipe_tag_cross_refs",
    primaryKeys = ["recipeId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeTag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("tagId")]
)
data class RecipeTagCrossRef(
    val recipeId: Long,                    // 레시피 ID
    val tagId: Long                        // 태그 ID
)
```

### 2. 식품 재고 관련 테이블

#### InventoryItem (재고 아이템)
```
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientId: Long?,               // 재료 ID (연결된 재료가 있는 경우)
    val name: String,                      // 아이템 이름
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위
    val purchaseDate: Long?,               // 구매 날짜
    val expiryDate: Long?,                 // 유통기한
    val barcode: String?,                  // 바코드
    val storageLocation: String?,          // 보관 위치(냉장, 냉동, 실온 등)
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
```

#### ShoppingListItem (쇼핑 목록 아이템)
```
@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientId: Long?,               // 재료 ID (연결된 재료가 있는 경우)
    val name: String,                      // 아이템 이름
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위
    val isChecked: Boolean = false,        // 구매 완료 여부
    val category: String?,                 // 카테고리
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis()   // 생성 시간
)
```

### 3. 타이머 관련 테이블

#### Timer (타이머)
```
@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 타이머 이름
    val duration: Long,                    // 타이머 시간(밀리초)
    val recipeId: Long?,                   // 연결된 레시피 ID
    val recipeStepId: Long?,               // 연결된 레시피 단계 ID
    val isRunning: Boolean = false,        // 실행 중 여부
    val startTime: Long?,                  // 시작 시간
    val remainingTime: Long?,              // 남은 시간
    val soundUri: String?,                 // 알림음 URI
    val vibration: Boolean = true,         // 진동 여부
    val createdAt: Long = System.currentTimeMillis()   // 생성 시간
)
```

### 4. 식단 계획 관련 테이블

#### MealPlan (식단 계획)
```
@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,                        // 날짜
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
```

#### MealPlanItem (식단 계획 아이템)
```
@Entity(
    tableName = "meal_plan_items",
    foreignKeys = [
        ForeignKey(
            entity = MealPlan::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("mealPlanId"), Index("recipeId")]
)
data class MealPlanItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealPlanId: Long,                  // 식단 계획 ID
    val recipeId: Long?,                   // 레시피 ID
    val mealType: String,                  // 식사 유형(아침, 점심, 저녁, 간식)
    val servings: Int,                     // 인분 수
    val notes: String?                     // 메모
)
```

### 5. 사용자 관련 테이블

#### User (사용자)
```
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 사용자 이름
    val profileImageUrl: String?,          // 프로필 이미지 URL
    val dietaryRestrictions: String?,      // 식이 제한 사항
    val allergies: String?,                // 알레르기
    val preferences: String?,              // 선호도
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
```

### 6. 블루투스 기기 관련 테이블

#### BluetoothDevice (블루투스 기기)
```
@Entity(tableName = "bluetooth_devices")
data class BluetoothDevice(
    @PrimaryKey val address: String,       // 기기 MAC 주소
    val name: String?,                     // 기기 이름
    val type: String,                      // 기기 유형(온도계, 저울 등)
    val lastConnected: Long?,              // 마지막 연결 시간
    val isConnected: Boolean = false,      // 연결 상태
    val isPaired: Boolean = false,         // 페어링 상태
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
```

## 관계 및 조인 쿼리

### 레시피와 재료 조회
```kotlin
@Transaction
@Query("SELECT * FROM recipes WHERE id = :recipeId")
fun getRecipeWithIngredients(recipeId: Long): RecipeWithIngredients

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        entity = RecipeIngredient::class,
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val recipeIngredients: List<RecipeIngredientWithIngredient>
)

data class RecipeIngredientWithIngredient(
    @Embedded val recipeIngredient: RecipeIngredient,
    @Relation(
        parentColumn = "ingredientId",
        entityColumn = "id"
    )
    val ingredient: Ingredient
)
```

### 레시피와 단계 조회
```kotlin
@Transaction
@Query("SELECT * FROM recipes WHERE id = :recipeId")
fun getRecipeWithSteps(recipeId: Long): RecipeWithSteps

data class RecipeWithSteps(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStep>
)
```

### 레시피와 태그 조회
```kotlin
@Transaction
@Query("SELECT * FROM recipes WHERE id = :recipeId")
fun getRecipeWithTags(recipeId: Long): RecipeWithTags

data class RecipeWithTags(
    @Embedded val recipe: Recipe,
    @Relation(
        entity = RecipeTagCrossRef::class,
        parentColumn = "id",
        entityColumn = "recipeId",
        associateBy = Junction(
            value = RecipeTagCrossRef::class,
            parentColumn = "recipeId",
            entityColumn = "tagId"
        )
    )
    val tags: List<RecipeTag>
)
```

### 식단 계획과 레시피 조회
```kotlin
@Transaction
@Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate")
fun getMealPlansWithItems(startDate: Long, endDate: Long): List<MealPlanWithItems>

data class MealPlanWithItems(
    @Embedded val mealPlan: MealPlan,
    @Relation(
        entity = MealPlanItem::class,
        parentColumn = "id",
        entityColumn = "mealPlanId"
    )
    val mealPlanItems: List<MealPlanItemWithRecipe>
)

data class MealPlanItemWithRecipe(
    @Embedded val mealPlanItem: MealPlanItem,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe?
)
```

## 데이터 접근 객체 (DAO)

### RecipeDao
```kotlin
@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
    
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Long): Recipe?
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredients(recipeId: Long): Flow<RecipeWithIngredients>
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithSteps(recipeId: Long): Flow<RecipeWithSteps>
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithTags(recipeId: Long): Flow<RecipeWithTags>
    
    @Transaction
    @Query("SELECT * FROM recipes")
    fun getRecipesWithIngredients(): Flow<List<RecipeWithIngredients>>
}
```

### InventoryDao
```kotlin
@Dao
interface InventoryDao {
    @Insert
    suspend fun insertInventoryItem(item: InventoryItem): Long
    
    @Update
    suspend fun updateInventoryItem(item: InventoryItem)
    
    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)
    
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE expiryDate < :date ORDER BY expiryDate ASC")
    fun getExpiringItems(date: Long): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%'")
    fun searchInventoryItems(query: String): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode")
    suspend fun getInventoryItemByBarcode(barcode: String): InventoryItem?
}
```

### ShoppingListDao
```kotlin
@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insertShoppingListItem(item: ShoppingListItem): Long
    
    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)
    
    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItem)
    
    @Query("SELECT * FROM shopping_list_items ORDER BY category, name ASC")
    fun getAllShoppingListItems(): Flow<List<ShoppingListItem>>
    
    @Query("SELECT * FROM shopping_list_items WHERE isChecked = 0 ORDER BY category, name ASC")
    fun getActiveShoppingListItems(): Flow<List<ShoppingListItem>>
    
    @Query("UPDATE shopping_list_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateItemCheckedStatus(id: Long, isChecked: Boolean)
    
    @Query("DELETE FROM shopping_list_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()
}
```

### TimerDao
```kotlin
@Dao
interface TimerDao {
    @Insert
    suspend fun insertTimer(timer: Timer): Long
    
    @Update
    suspend fun updateTimer(timer: Timer)
    
    @Delete
    suspend fun deleteTimer(timer: Timer)
    
    @Query("SELECT * FROM timers ORDER BY createdAt DESC")
    fun getAllTimers(): Flow<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE isRunning = 1")
    fun getRunningTimers(): Flow<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE id = :timerId")
    suspend fun getTimerById(timerId: Long): Timer?
    
    @Query("UPDATE timers SET isRunning = 0")
    suspend fun stopAllTimers()
}
```

### MealPlanDao
```kotlin
@Dao
interface MealPlanDao {
    @Insert
    suspend fun insertMealPlan(mealPlan: MealPlan): Long
    
    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)
    
    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
    
    @Transaction
    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getMealPlansWithItems(startDate: Long, endDate: Long): Flow<List<MealPlanWithItems>>
    
    @Query("SELECT * FROM meal_plans WHERE date = :date")
    suspend fun getMealPlanByDate(date: Long): MealPlan?
    
    @Insert
    suspend fun insertMealPlanItem(item: MealPlanItem): Long
    
    @Update
    suspend fun updateMealPlanItem(item: MealPlanItem)
    
    @Delete
    suspend fun deleteMealPlanItem(item: MealPlanItem)
}
```

## 데이터베이스 마이그레이션

Room 데이터베이스 버전 관리 및 마이그레이션 전략을 통해 앱 업데이트 시 사용자 데이터를 보존합니다.

```kotlin
@Database(
    entities = [
        Recipe::class, RecipeStep::class, Ingredient::class, RecipeIngredient::class,
        RecipeTag::class, RecipeTagCrossRef::class, InventoryItem::class,
        ShoppingListItem::class, Timer::class, MealPlan::class, MealPlanItem::class,
        User::class, BluetoothDevice::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun timerDao(): TimerDao
    abstract fun mealPlanDao(): MealPlanDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databas<response clipped><NOTE>To save on context only part of this file has been shown to you. You should retry this tool after you have searched inside the file with `grep -n` in order to find the line numbers of what you are looking for.</NOTE>