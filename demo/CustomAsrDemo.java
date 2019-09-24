import com.huawei.custom.asr.bean.AsrConfig;
import com.huawei.custom.asr.bean.AsrLongResponse;
import com.huawei.custom.asr.bean.AsrShortRequest;
import com.huawei.custom.asr.bean.base.AnalysisInfo;
import com.huawei.custom.asr.bean.base.Sentence;
import com.huawei.custom.asr.bean.AsrConstant;
import com.huawei.custom.asr.bean.AsrLongRequest;
import com.huawei.custom.asr.bean.AsrShortResponse;
import com.huawei.custom.asr.bean.AuthInfo;
import com.huawei.custom.asr.client.CustomAsrClient;
import com.huawei.custom.asr.exception.AsrException;
import com.huawei.custom.asr.util.HttpClientUtils;

import java.util.List;

/**
 * 测试用的demo
 */
public class CustomAsrDemo {
  private static final int SLEEP_TIME = 500;
  private static final int MAX_POLLING_NUMS = 1000;

  private String ak = "C9Q7KQDMX6W8LKN0FXZK";
  private String sk = "I97cz020eBrvKkzDqI5HIzCNAA05a1XdHtOZknfS";
  private String region = "cn-north-4";
  private String projectId = "064d1ca18d0010982fa9c017b3decce6";

  private String obsUrl = "http://d.pcs.baidu.com/file/b3330f31938e49025cd92aff3189c4c6?fid=1344296490-250528-362381377411880&rt=pr&sign=FDtAERVCY-DCb740ccc5511e5e8fedcff06b081203-Z%2FKYZfvTVEN8kmdlpwDQXUBfaTg%3D&expires=8h&chkv=1&chkbd=1&chkpc=et&dp-logid=6188233219643973941&dp-callid=0&dstime=1569324965&r=455083034";
  private String path = "";
  private String obsAudioFormat = "auto";
  private String pathAudioFormat = "";
  private String property = "chinese_8k_common";

  /**
   * 所有参数均有默认值，不配置也可使用
   *
   * @return request
   */
  private void setShortParameter(AsrShortRequest request) {

    // 设置是否添加标点，默认是no
    request.setAddPunc("yes");
	// 设置热词id，详见api文档，若热词id不存在，则会报错
    // request.setVocabularyId("");

  }

  /**
   * 所有参数均有默认值，不配置也可使用
   *
   * @return request
   */
  private void setLongParameter(AsrLongRequest request) {
    // 设置否是添加标点，yes 或no， 默认是no
    request.setAddPunc("yes");
    // 设置声道，MONO/LEFT_AGENT/RIGHT_AGENT, 默认是单声道MONO
    request.setChannel("MONO");
    // 设置是否需要话者分离，若是，则识别结果包含role，默认true
    request.setDirization(true);
    // 设置是否需要情绪检测，默认ture
    request.setEmotion(true);
    // 设置是否需要分析，默认为false
    request.setNeedAnalysis(true);
	// 设置是否需要速度信息,默认为true
    request.setSpeed(false);
	// 设置热词id，详见api文档
    // request.setVocabularyId("");
  }

  /**
   * 定义config，所有参数可选，设置超时时间。
   *
   * @return AsrConfig
   */
  private AsrConfig getConfig() {
    AsrConfig config = new AsrConfig();
    // 设置连接超时，默认5000ms
    config.setConnectionTimeout(AsrConstant.DEFAULT_CONNECTION_TIMEOUT);
    // 设置请求超时，默认1000ms
    config.setRequestTimeout(AsrConstant.DEFAULT_CONNECTION_REQUEST_TIMEOUT);
    // 设置socket超时，默认5000ms
    config.setSocketTimeout(AsrConstant.DEFAULT_SOCKET_TIMEOUT);
    return config;
  }

  /**
   * 打印录音文件识别结果
   *
   * @param response 录音文件识别响应
   */
  private void printAsrLongResponse(AsrLongResponse response) {
    System.out.println("status=" + response.getStatus());
    System.out.println("startTime=" + response.getStartTime());
    System.out.println("createTime=" + response.getCreateTime());
    System.out.println("finishTime=" + response.getFinishTime());
    System.out.println("segments=");
    List<Sentence> sentenceList = response.getSentenceList();
    for (int i = 0; i < sentenceList.size(); i++) {
      System.out.println("\t{");
      Sentence sentence = sentenceList.get(i);
      System.out.println("\t\tsentenceStartTime=" + sentence.getStartTime());
      System.out.println("\t\tsentenceEndTime=" + sentence.getEndTime());
      System.out.println("\t\tsentenceText=" + sentence.getText());
      AnalysisInfo analysisInfo = sentence.getAnalysisInfo();
      if (analysisInfo != null) {
        if (!analysisInfo.getRole().equals("")) {
          System.out.println("\t\trole=" + analysisInfo.getRole());
        }
        if (!analysisInfo.getEmotion().equals("")) {
          System.out.println("\t\temotion=" + analysisInfo.getEmotion());
        }
        if (analysisInfo.getSpeed() != -1) {
          System.out.println("\t\tspeed=" + analysisInfo.getSpeed());
        }

      }
      System.out.println("\t}");
    }
    System.out.println("\n");
  }

  /**
   * 打印一句话识别结果
   *
   * @param response 一句话识别响应
   */
  private void printAsrShortResponse(AsrShortResponse response) {
    System.out.println("traceId=" + response.getTraceId());
    System.out.println("text=" + response.getText());
    System.out.println("score=" + response.getScore());
    System.out.println("\n");
  }

  /**
   * 一句话识别demo
   */
//  private void shortDemo() {
//    try {
//
//      // 1. 初始化CustomAsrClient
//      // 1.1 定义authInfo，根据ak，sk，region，projectId。可不填endPoint
//      AuthInfo authInfo = new AuthInfo(ak, sk, region, projectId);
//
//      // 1.2 设置config，主要与超时有关
//      AsrConfig config = getConfig();
//
//      // 1.3 根据authInfo和config，构造CustomAsrClient
//      CustomAsrClient asr = new CustomAsrClient(authInfo, config);
//
//      // 2. 生成请求
//      // data: 语言数据的base64编码，要求编码大小不超过4m，音频时长不超过1min
//      // pathAudioFormat： 语音格式，目前支持pcm, ulaw, alaw, wav, amr等。裸音频需标注采样率位宽信息，详见api文档
//      // property：模型特征串，如chinese_8k_common，表示语言_采样率_领域，详见api文档
//      String data = HttpClientUtils.getEncodeDataByPath(path);
//      AsrShortRequest request = new AsrShortRequest(data, pathAudioFormat,
//          property);
//
//      // 3.设置请求参数，所有参数均为可选
//      setShortParameter(request);
//
//      // 4. 获取响应
//      AsrShortResponse response = asr.getAsrShortResponse(request);
//
//      // 5. 打印结果
//      printAsrShortResponse(response);
//
//      // 6. 关闭，当所有服务都调用完毕再关闭
//      asr.close();
//
//    } catch (AsrException e) {
//      e.printStackTrace();
//      System.out.println("error_code: " + e.getErrorCode() + "\nerror_msg: " + e.getErrorMsg());
//    }
//  }

  /**
   * 录音文件识别demo
   */
  private void longDemo() {
    try {
      // 1. 初始化CustomAsrClient
      // 1.1 定义authInfo，根据ak，sk，region,projectId. 可不填endPoint
      AuthInfo authInfo = new AuthInfo(ak, sk, region, projectId);

      // 1.2 设置config，主要与超时有关
      AsrConfig config = getConfig();

      // 1.3 根据authInfo和config，构造CustomAsrClient
      CustomAsrClient asr = new CustomAsrClient(authInfo, config);

      // 2. 生成请求
      // obsUrl: 音频的obs连接
      // obsAudioFormat: 支持语音格式，如auto，pcm等，详见api文档
      // property: 模型特征串，如chinese_8k_common，表示语言_采样率_领域，详见api文档
      AsrLongRequest request = new AsrLongRequest(obsUrl, obsAudioFormat,
          property);

      // 设置否是添加标点，yes 或no， 默认是no
      request.setAddPunc("yes");
      // 设置声道，MONO/LEFT_AGENT/RIGHT_AGENT, 默认是单声道MONO
      request.setChannel("MONO");
      // 设置是否需要话者分离，若是，则识别结果包含role，默认true
      request.setDirization(true);
      // 设置是否需要情绪检测，默认ture
      request.setEmotion(true);
      // 设置是否需要分析，默认为false，若为false，则speed，emotion，channel，dirization均无效。
      request.setNeedAnalysis(true);
      // 设置是否需要速度信息,默认为true
      request.setSpeed(false);

      // 3.设置请求参数，所有参数均为可选
      setLongParameter(request);

      // 4. 提交任务，获取jobId
      String jobId = asr.submitJob(request);

      // 5 轮询jobId，获取最终结果。
      int count = 0;
      AsrLongResponse response = null;
      while (count < MAX_POLLING_NUMS) {
        System.out.println("正在进行第" + count + "次尝试");
        response = asr.getAsrLongResponse(jobId);
        String status = response.getStatus();
        if (status.equals("FINISHED")) {
          break;
        } else if (status.equals("ERROR")) {
          System.out.println("执行失败, 无法根据jobId获取结果");
          return;
        }
        try {
          Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        count++;
      }

      // 5. 打印结果
      printAsrLongResponse(response);

      // 6. 关闭，当所有服务都调用完毕再关闭
      asr.close();
    } catch (AsrException e) {
      e.printStackTrace();
      System.out.println("error_code: " + e.getErrorCode() + "\nerror_msg: " + e.getErrorMsg());
    }
  }

  public static void main(String[] args) {
    CustomAsrDemo demo = new CustomAsrDemo();
    // 选择1 一句话识别
//    demo.shortDemo();

    // 选择2 录音文件识别
    demo.longDemo();
  }

}
