package com.smartkitchen.assistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartkitchen.assistant.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 앱 데이터베이스 클래스
 * Room 데이터베이스 인스턴스를 제공하고 관리합니다.
 */
@Database(
    entities = [
        Recipe::class, RecipeStep::class, Ingredient::class, RecipeIngredient::class,
        InventoryItem::class, ShoppingListItem::class, Timer::class, 
        MealPlan::class, MealPlanItem::class
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
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_kitchen_database"
                )
                .fallbackToDestructiveMigration()  // 개발 단계에서만 사용, 출시 전에 적절한 마이그레이션 구현 필요
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 데이터베이스 생성 시 초기 데이터 삽입
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            
                            // 기본 재료 카테고리 삽입 예시
                            val ingredients = listOf(
                                Ingredient(name = "소고기", category = "육류", imageUrl = null),
                                Ingredient(name = "돼지고기", category = "육류", imageUrl = null),
                                Ingredient(name = "닭고기", category = "육류", imageUrl = null),
                                Ingredient(name = "연어", category = "해산물", imageUrl = null),
                                Ingredient(name = "새우", category = "해산물", imageUrl = null),
                                Ingredient(name = "양파", category = "채소", imageUrl = null),
                                Ingredient(name = "마늘", category = "채소", imageUrl = null),
                                Ingredient(name = "토마토", category = "채소", imageUrl = null),
                                Ingredient(name = "감자", category = "채소", imageUrl = null),
                                Ingredient(name = "당근", category = "채소", imageUrl = null),
                                Ingredient(name = "사과", category = "과일", imageUrl = null),
                                Ingredient(name = "바나나", category = "과일", imageUrl = null),
                                Ingredient(name = "우유", category = "유제품", imageUrl = null),
                                Ingredient(name = "치즈", category = "유제품", imageUrl = null),
                                Ingredient(name = "요거트", category = "유제품", imageUrl = null),
                                Ingredient(name = "쌀", category = "곡물", imageUrl = null),
                                Ingredient(name = "밀가루", category = "곡물", imageUrl = null),
                                Ingredient(name = "소금", category = "조미료", imageUrl = null),
                                Ingredient(name = "설탕", category = "조미료", imageUrl = null),
                                Ingredient(name = "후추", category = "조미료", imageUrl = null)
                            )
                            
                            // 기본 재료 삽입
                            ingredients.forEach { ingredient ->
                                database.recipeDao().insertIngredient(ingredient)
                            }
                            
                            // 샘플 레시피 추가
                            val recipeId = database.recipeDao().insertRecipe(
                                Recipe(
                                    name = "간단한 토마토 파스타",
                                    description = "신선한 토마토와 마늘로 만드는 간단하고 맛있는 파스타 요리",
                                    prepTime = 10,
                                    cookTime = 20,
                                    servings = 2,
                                    difficulty = "쉬움",
                                    imageUrl = null,
                                    source = "스마트 요리 보조 시스템",
                                    notes = "토마토가 신선할수록 맛있습니다."
                                )
                            )
                            
                            // 레시피 단계 추가
                            val steps = listOf(
                                RecipeStep(
                                    recipeId = recipeId,
                                    stepNumber = 1,
                                    description = "파스타 면을 패키지 지시에 따라 삶습니다.",
                                    timerDuration = 10 * 60
                                ),
                                RecipeStep(
                                    recipeId = recipeId,
                                    stepNumber = 2,
                                    description = "팬에 올리브 오일을 두르고 다진 마늘을 볶습니다.",
                                    timerDuration = 2 * 60
                                ),
                                RecipeStep(
                                    recipeId = recipeId,
                                    stepNumber = 3,
                                    description = "토마토를 깍둑썰기하여 팬에 넣고 중불에서 5분간 익힙니다.",
                                    timerDuration = 5 * 60
                                ),
                                RecipeStep(
                                    recipeId = recipeId,
                                    stepNumber = 4,
                                    description = "삶은 파스타 면을 소스에 넣고 잘 섞습니다.",
                                    timerDuration = 1 * 60
                                ),
                                RecipeStep(
                                    recipeId = recipeId,
                                    stepNumber = 5,
                                    description = "소금과 후추로 간을 하고 바질을 뿌려 완성합니다.",
                                    timerDuration = null
                                )
                            )
                            
                            // 레시피 단계 삽입
                            steps.forEach { step ->
                                database.recipeDao().insertRecipeStep(step)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
