package Worker.business_logic.services;

import Worker.api.dto.CrackTaskRequestDTO;
import Worker.api.dto.StatusDTO;
import Worker.business_logic.config.ManagerConfig;
import Worker.business_logic.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CrackHashWorkerService
{
    public CrackHashWorkerService(ManagerConfig managerConfig,RabbitTemplate rabbitTemplate)
    {
        this.managerConfig=managerConfig;
        this.rabbitTemplate=rabbitTemplate;
    }

    public void addToQueue(CrackTaskRequestDTO taskRequest)
    {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                taskRequest);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    private void processTask(CrackTaskRequestDTO taskRequest)
    {
        List<String> foundWords = new ArrayList<>();

//        foundWords.add("good");
//        sendResultToManager(taskRequest.getRequestId(), foundWords);

        progresses.put(taskRequest.getRequestId(),0);

        int totalWords = (int) Math.pow(alphabet.length(), taskRequest.getMaxLength());

        int startIndex = (totalWords / taskRequest.getPartCount()) * taskRequest.getPartNumber();
        int endIndex = (totalWords / taskRequest.getPartCount()) * (taskRequest.getPartNumber() + 1);


        System.out.println("startIndex: " + startIndex+", endIndex: " + endIndex+", totalWords: " + totalWords);

        int totalIterations = endIndex - startIndex;
        int currentIteration = 0;

        for (int i = startIndex; i < endIndex; i++) {
            String word = generateWord(i, taskRequest.getMaxLength(), i - startIndex, totalIterations, taskRequest.getRequestId());
            if (md5(word).equals(taskRequest.getHash())) {
                foundWords.add(word);
            }
        }

        if (!foundWords.isEmpty())
        {
            foundText=String.join("", foundWords);
            sendResultToManager(taskRequest.getRequestId(), foundWords);
        }

        progresses.remove(taskRequest.getRequestId());
//
//        System.out.println("lali");

    }

    public StatusDTO getStatus(String requestId)
    {
        return new StatusDTO(progresses.get(requestId));
    }

    private String generateWord(int index, int length, int currentIteration, int totalIterations, String requestId) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            word.insert(0, alphabet.charAt(index % alphabet.length()));
            index /= alphabet.length();

            progresses.put(requestId, (int) (((currentIteration + (i / (double) length)) / totalIterations) * 100));

            //progress = (int) (((currentIteration + (i / (double) length)) / totalIterations) * 100);
        }
        return word.toString();
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }


    private void sendResultToManager(String requestId, List<String> foundWords)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("data", foundWords);

        restTemplate.postForObject(managerConfig.getUrl(), result, Void.class);
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private final String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";

    private final ManagerConfig managerConfig;

    private int progress=0;

    private String foundText=null;

    private final RabbitTemplate rabbitTemplate;

    Map<String, Integer> progresses = new ConcurrentHashMap<>();
}

//что происходит когда новый пользователь приходит?
//ограничение на количество строк в программе