package com.zugusiot.AppInitializer;

import javax.servlet.*;
  import java.util.Set;

public class FiltrageCors {
  
  
  public class AppInitializer implements ServletContainerInitializer {
      
      @Override
      public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
          // Créer une instance de votre filtre CORS
          CorsFilter corsFilter = new CorsFilter();
          
          // Enregistrer et configurer le filtre CORS
          FilterRegistration.Dynamic fr = ctx.addFilter("corsFilter", corsFilter);
          fr.addMappingForUrlPatterns(null, false, "/*");
          fr.setInitParameter("cors.allowed.origins", "*");
          fr.setInitParameter("cors.allowed.methods", "GET, POST, PUT, DELETE, OPTIONS");
          fr.setInitParameter("cors.allowed.headers", "Content-Type");
      }
  }
  

import com.zugusiot.HttpSensorServer.CorsFilter;

}
