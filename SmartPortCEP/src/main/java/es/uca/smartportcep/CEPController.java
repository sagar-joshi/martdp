package es.uca.smartportcep;

import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.EsperUtils;

import java.util.Map;

@RestController
public class CEPController {

    private final static Logger logger = LoggerFactory.getLogger(CEPController.class);
    private final String basePath = "/api/v1";

    @PostMapping(value = basePath + "/schema", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> schema(@RequestBody Map<String, Object> payload) {
        logger.info("/POST schema - Deploying schema");
        try {
            EsperUtils.addNewSchema(payload.get("schema").toString());
        } catch (EPCompileException | EPDeployException e) {
            e.printStackTrace();
            return new ResponseEntity<>("{msg:\"Problem deploying the schema!\", err: \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>("{msg:\"Schema deployed!\"}", HttpStatus.OK);
    }

    @PostMapping(value = basePath + "/pattern", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> pattern(@RequestBody Map<String, Object> payload) {
        logger.info("/POST pattern - Deploying pattern");
        try {
            for (String pattern : payload.get("pattern").toString().split(";"))
                EsperUtils.addGenericListener(EsperUtils.deployNewEventPattern(pattern).getStatements()[0]);
        } catch (EPCompileException | EPDeployException e) {
            e.printStackTrace();
            return new ResponseEntity<>("{msg:\"Problem deploying the pattern!\", err: \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("{msg:\"Pattern deployed!\"}", HttpStatus.OK);
    }

    @PostMapping(value = basePath + "/dataflow", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> dataflow(@RequestBody Map<String, Object> payload) {
        logger.info("/POST dataflow - Deploying dataflow");
        try {
            EsperUtils.createDataflow(payload.get("dataflow").toString(), payload.get("name").toString());
        } catch (EPCompileException | EPDeployException e) {
            e.printStackTrace();
            return new ResponseEntity<>("{msg:\"Problem deploying the dataflow!\", err: \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("{msg:\"Dataflow deployed!\"}", HttpStatus.OK);
    }
}