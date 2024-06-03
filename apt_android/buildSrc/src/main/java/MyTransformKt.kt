import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File

class MyTransformKt : Transform() {
    /**
     * transform 名字
     */
    override fun getName(): String = "MyTransformKt"

    /**
     * 输入文件的类型
     * 可供我们去处理的有两种类型, 分别是编译后的java代码, 以及资源文件(非res下文件, 而是assests内的资源)
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    /**
     * 是否支持增量
     * 如果支持增量执行, 则变化输入内容可能包含 修改/删除/添加 文件的列表
     */
    override fun isIncremental(): Boolean = true

    /**
     * 指定作用范围
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    /**
     * transform的执行主函数
     */
    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        log("start transform")

        //如果不是增量模式，需要删除上一次构建产生的所有中间文件
        if (transformInvocation?.isIncremental != true) {
            log("non incremental, delete last output")
            transformInvocation?.outputProvider?.deleteAll()
        }

        transformInvocation?.inputs?.forEach {
            // 输入源为文件夹类型
            it.directoryInputs.forEach { directoryInput ->
                with(directoryInput) {

                    val destDir = transformInvocation.outputProvider.getContentLocation(
                        name,
                        contentTypes,
                        scopes,
                        Format.DIRECTORY
                    )
                    log("transform directory： input file: $file, output file: $destDir")

                    if (transformInvocation.isIncremental) {
                        changedFiles.forEach { (changedFile, status) ->
                            log("directory file $status")
                            when (status) {
                                Status.NOTCHANGED, null -> {
                                    // Do nothing.
                                }

                                Status.ADDED, Status.CHANGED -> {
                                    // Do transform.
                                    // TODO 针对文件进行字节码操作
                                    changedFile.copyTo(
                                        File(destDir, changedFile.name),
                                        true /*override*/
                                    )
                                }

                                Status.REMOVED -> {
                                    // Delete
                                    File(destDir, changedFile.name).delete()
                                }
                            }
                        }
                    } else {
                        // TODO 针对文件夹进行字节码操作
                        // 将 input 的目录复制到 output 指定目录
                        file.copyRecursively(destDir)
                    }

                }
            }

            // 输入源为jar包类型
            it.jarInputs.forEach { jarInput ->
                with(jarInput) {
                    val dest = transformInvocation.outputProvider.getContentLocation(
                        name,
                        contentTypes,
                        scopes,
                        Format.JAR
                    )
                    log("transform jar： input file: $file, output file: $dest")
                    if (transformInvocation.isIncremental) {
                        log("jarInput ${jarInput.status}")
                        when (jarInput.status) {
                            Status.NOTCHANGED, null -> {
                                // Do nothing.
                            }

                            Status.ADDED, Status.CHANGED -> {
                                // Do transform.
                                // TODO 针对Jar文件进行相关处理
                                file.copyTo(dest, true /*override*/)
                            }

                            Status.REMOVED -> {
                                // Delete.
                                dest.delete()
                            }
                        }
                    } else {
                        // TODO 针对Jar文件进行相关处理
                        file.copyTo(dest)
                    }
                }
            }
        }
    }

    private fun log(msg: String) {
        println("$name >>>>>>>>>> $msg")
    }
}