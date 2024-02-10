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

            val images = File(jobConfig.imageFolder).listFiles { file ->
                file.isFile && file.extension.lowercase().let { it == "jpg" || it == "png" }
            }!!.associateBy { it.name.lowercase() }

            if (images.isEmpty()) {
                return@forEach
            }

            this.images[jobConfig.name] = images

            val masterList = images.keys.sorted().map {
                ComparisonContainer(it)
            }

            val store = PersistentMajorityVotingComparisonStore(jobConfig.votes, File(jobConfig.name))

            this.stores[jobConfig.name] = store

            val job = SortJob(Random(0), masterList, store)

            this.jobs[jobConfig.name] = job

        }
    }

    fun getImage(jobName: String, imageName: String): File? = this.images[jobName]?.get(imageName)

    fun getJob(jobName: String) = this.jobs[jobName]

    fun nextJobName(): String {
        val active = jobs.filter { !it.value.complete }

        return if (active.isNotEmpty()) {
            active.keys.random()
        } else {
            jobs.keys.random()
        }

    }

    fun flushAll() {
        this.stores.values.forEach { it.flush() }
    }

}