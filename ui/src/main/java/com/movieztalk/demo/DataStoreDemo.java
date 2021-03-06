package com.movieztalk.demo;

import static com.google.api.services.datastore.client.DatastoreHelper.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.DatastoreV1.CommitResponse;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Mutation;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.protobuf.ByteString;

public class DataStoreDemo {
  public static void main(String args[]) {
    String datasetId = "purchhaseme";
    Datastore datastore = null;
    try {
      // Setup the connection to Google Cloud Datastore and infer credentials
      // from the environment.
      datastore = DatastoreFactory.get().create(
          DatastoreHelper.getOptionsFromEnv().dataset(datasetId).build());
    } catch (GeneralSecurityException exception) {
      System.err.println("Security error connecting to the datastore: " + exception.getMessage());
      System.exit(1);
    } catch (IOException exception) {
      System.err.println("I/O error connecting to the datastore: " + exception.getMessage());
      System.exit(1);
    }

    try {
      // Create an RPC request to begin a new transaction.
      BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
      // Execute the RPC synchronously.
      BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
      // Get the transaction handle from the response.
      ByteString tx = tres.getTransaction();

      // Create an RPC request to get entities by key.
      // Set the entity key with only one `path_element`: no parent.
      CommitRequest.Builder creq = CommitRequest.newBuilder();
      // Set the transaction to commit.
      creq.setTransaction(tx);
      List<String> movieList = Arrays.asList("3 Idiots", "Rang De Basanti", "Like Stars on Earth",
          "Mughal-E-Azam", "Lagaan: Once Upon a Time in India", "Swades", "Pyaasa", "A Wednesday",
          "Anand", "Drishyam", "Gangs of Wasseypur", "Udaan", "Bhaag Milkha Bha    ag",
          "Special 26", "Dil Chahta Hai", "Black Friday", "Queen", "Haider", "Paan Singh Tomar",
          "Guru", "Andaz Apna Apna", "Dev D", "Chakde India", "Sarfarosh", "Barfi",
          "Zindagi Na Milegi Dobara", "PK", "Baby", "My Name     Is Khan",
          "The Legend of Bhagat Singh", "Kahaani", "Bajrangi Bhaijaan", "Shahid", "Salaam Bombay",
          "Black", "Gol Maal", "Omkara", "Guide", "Kaagaz Ke Phool", "Jo Jeeta Wohi Sikandar",
          "Vaastav The Reality", "The     Chess Players", "Mother India", "Water", "Sholay",
          "Hera Pheri", "Chupke Chupke", "Manjhi The Mountain Man", "Roja",
          "Straight from the Heart", "Deewaar", "Who Pays the Piper", "Company", "Hindustani",
          "Jaane Tu Y    a Jaane Na", "Rock On", "Lage Raho Munna Bhai", "Johnny Gaddaar",
          "Padosan", "Do Bigha Zamin", "Jab We Met", "Mumbai Meri Jaan", "Oye Lucky Lucky Oye",
          "Kal Ho Naa Ho", "Chhoti Si Baat", "Awaara", "Dilwale Dulhan    ia Le Jayenge", "Ugly",
          "Bombay", "Gangaajal", "Vicky Donor", "Don", "Parinda", "Ghulam",
          "Qayamat Se Qayamat Tak", "Dil Se", "Jodhaa Akbar", "Mera Naam Joker", "Lootera",
          "Ankur", "I Am Kalam", "Devdas", "Satya", "Sa    aransh", "Maine Pyar Kiya", "Zanjeer",
          "Hum Aapke Hain Koun", "Masoom", "Don", "Maqbool", "Agneepath", "Shree 420",
          "Damini  Lightning", "Kai po che", "Rockstar", "Delhi Belly", "OMG Oh My God");

      CommitRequest.Builder commBuilder = CommitRequest.newBuilder()
          .setMode(CommitRequest.Mode.NON_TRANSACTIONAL).setMutation(Mutation.newBuilder());

      for (String moString : movieList) {
        Entity.Builder employee = Entity.newBuilder().setKey(makeKey("movie"))
            .addProperty(makeProperty("name", makeValue(moString)))
            .addProperty(makeProperty("industrytype", makeValue("bollywood")));
        Entity entity = employee.build();
        commBuilder.getMutationBuilder().addInsertAutoId(entity);
      }

      /*
       * Set<Entity> entities = new HashSet<>(); for (String movie : movieList) { Entity entity;
       * Entity.Builder employee = Entity.newBuilder().setKey(makeKey("movie"))
       * .addProperty(makeProperty("name", makeValue(movie))); entity = employee.build();
       * entities.add(entity); }
       */

      // Insert the entity in the commit request mutation.
      // creq.getMutationBuilder().addAllInsertAutoId(entities);
      // Execute the Commit RPC synchronously and ignore the response.
      // Apply the insert mutation if the entity was not found and close
      // the transaction.
      datastore.commit(commBuilder.build());

    } catch (DatastoreException exception) {
      // Catch all Datastore rpc errors.
      System.err.println("Error while doing datastore operation");
      // Log the exception, the name of the method called and the error code.
      System.err.println(String.format("DatastoreException(%s): %s %s", exception.getMessage(),
          exception.getMethodName(), exception.getCode()));
      System.exit(1);
    }
  }
}
