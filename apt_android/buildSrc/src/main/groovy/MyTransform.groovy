import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager

class MyTransform extends Transform {


    @Override
    String getName() {
        return "MyTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        transformInvocation?.inputs?.forEach {
            // 输入源为文件夹类型
            it.directoryInputs.forEach {directoryInput->
                with(directoryInput){
                    // TODO 针对文件夹进行字节码操作
                    def dest = transformInvocation.outputProvider.getContentLocation(
                            name,
                            contentTypes,
                            scopes,
                            Format.DIRECTORY
                    )
                    file.copyTo(dest)
                }
            }

            // 输入源为jar包类型
            it.jarInputs.forEach { jarInput->
                with(jarInput){
                    // TODO 针对Jar文件进行相关处理
                    def dest = transformInvocation.outputProvider.getContentLocation(
                            name,
                            contentTypes,
                            scopes,
                            Format.JAR
                    )
                    file.copyTo(dest)
                }
            }
        }
    }
}