@RestController
@RequestMapping("/api")
public class SyncController {

    @Autowired private SyncService syncService;

    @GetMapping("/sync/{system}")
    public String sync(@PathVariable String system) {
        try {
            int count = syncService.syncUsers(system);
            return "Successfully imported " + count + " users from " + system;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
