package com.annotations.demo.controller;

import com.annotations.demo.entity.Annotateur;
import com.annotations.demo.entity.Annotation;
import com.annotations.demo.entity.Dataset;
import com.annotations.demo.entity.Task;
import com.annotations.demo.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur gérant les statistiques et métriques de l'application.
 * Accessible uniquement aux administrateurs.
 *
 * Points de terminaison :
 * - GET /admin/overview : Vue d'ensemble des statistiques
 *   - Nombre total d'annotations
 *   - Tâches actives
 *   - Total des datasets
 *   - Nombre d'annotateurs actifs
 *   - Progression par dataset
 *
 * Tests recommandés :
 * 1. Vérifier l'exactitude des statistiques globales
 * 2. Tester le calcul de progression des datasets
 * 3. Vérifier le format JSON des données pour les graphiques
 * 4. Tester avec différents états de données
 */
@Controller
@RequestMapping("/admin")
public class StatisticsController {
    private final TaskService taskService;
    private final AnnotateurService annotateurService;
    private final DatasetService datasetService;
    private final AnnotationService annotationService;
    private final UserService userService;

    @Autowired
    public StatisticsController(TaskService taskService,
                                AnnotateurService annotateurService,
                                DatasetService datasetService,
                                AnnotationService annotationService, UserService userService) {
        this.taskService = taskService;
        this.annotateurService = annotateurService;
        this.datasetService = datasetService;
        this.annotationService = annotationService;
        this.userService = userService;
    }

    /**
     * Affiche la vue d'ensemble des statistiques avec graphiques.
     *
     * @param model Le modèle Spring pour passer les données à la vue
     * @return Le nom de la vue à afficher
     * @throws JsonProcessingException Si la conversion en JSON échoue
     *
     * Test : Accéder à /admin/overview et vérifier :
     * - Les statistiques globales sont correctes
     * - Les données de progression sont bien formatées en JSON
     * - Le nom d'utilisateur est correctement capitalisé
     */
    @GetMapping("/overview")
    public String showStatistics(Model model) throws JsonProcessingException {
        // 1. Gather basic statistics
        long totalAnnotations = annotationService.countTotalAnnotations();
        long activeTasks = taskService.countActiveTasks();
        long totalDatasets = datasetService.countDatasets();
        long totalAnnotateurs = annotateurService.countActiveAnnotateurs();

        model.addAttribute("totalAnnotations", totalAnnotations);
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("totalDatasets", totalDatasets);
        model.addAttribute("totalAnnotateurs", totalAnnotateurs);

        // 2. Dataset progress data
        Map<String, Object> datasetsProgressData = new HashMap<>();
        List<String> datasetNames = new ArrayList<>();
        List<Integer> totalCouples = new ArrayList<>();
        List<Integer> annotatedCouples = new ArrayList<>();

        for (Dataset dataset : datasetService.findAllDatasets()) {
            datasetNames.add(dataset.getName());
            totalCouples.add(dataset.getCoupleTexts().size());
            annotatedCouples.add(annotationService.countAnnotationsByDataset(dataset.getId()));
        }

        datasetsProgressData.put("labels", datasetNames);
        datasetsProgressData.put("totalCouples", totalCouples);
        datasetsProgressData.put("annotatedCouples", annotatedCouples);

        //pass the map as a JSON for the script retrieval
        ObjectMapper objectMapper = new ObjectMapper();
        String datasetsProgressJson = objectMapper.writeValueAsString(datasetsProgressData);
        System.out.println(datasetsProgressJson);
        model.addAttribute("datasetsProgressJson", datasetsProgressJson);


        String currentUserName = StringUtils.capitalize(userService.getCurrentUserName());
        model.addAttribute("currentUserName", currentUserName);

        return "admin/statistics_management/overview";
    }

}
