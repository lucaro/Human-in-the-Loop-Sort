package ch.lucaro.hitls.api

import ch.lucaro.hitls.SortJob
import ch.lucaro.hitls.api.config.Config
import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.PersistentMajorityVotingComparisonStore
import java.io.File
import kotlin.random.Random

class SortJobManager(config: Config) {

    private val configs = config.jobs.associateBy { it.name }
    private val images = HashMap<String, Map<String, File>>()
    private val stores = HashMap<String, PersistentMajorityVotingComparisonStore>()
    private val jobs = HashMap<String, SortJob<String>>()

    init {
        configs.values.forEach { jobConfig ->

            val imagesFiles = File(jobConfig.imageFolder).listFiles { file ->
                file.isFile && file.extension.lowercase().let { it == "jpg" || it == "png" }
            }!!

            if (imagesFiles.isEmpty()) {
                return@forEach
            }

            val masterList = imagesFiles.sorted().map {
                ComparisonContainer(it.name.lowercase())
            }

            this.images[jobConfig.name] = imagesFiles.associateBy { f -> masterList.find { it.item == f.name.lowercase() }!!.id.toString() }

            val store = PersistentMajorityVotingComparisonStore(jobConfig.votes, File(jobConfig.name), masterList)

            this.stores[jobConfig.name] = store

            val job = SortJob(Random(0), masterList, store)

            this.jobs[jobConfig.name] = job

        }
    }

    fun getImage(jobName: String, imageId: String): File? = this.images[jobName]?.get(imageId)

    fun getJob(jobName: String) = this.jobs[jobName]

    fun nextJobName(): String {
        val active = jobs.filter { !it.value.complete }

        return if (active.isNotEmpty()) {
            active.keys.random()
        } else {
            jobs.keys.random()
        }

    }

    fun allJobNames(): List<String> = this.jobs.keys.sorted().toList()

    fun getStore(jobName: String) = this.stores[jobName]

    fun flushAll() {
        this.stores.values.forEach { it.flush() }
    }

    fun writeCompleted() {
        this.jobs.forEach {
            if (it.value.complete) {
                val writer = File("${it.key}_${System.currentTimeMillis()}.order").printWriter()
                it.value.sortedList?.forEach { item -> writer.println(item.item) }
                writer.flush()
            }
        }
    }

}