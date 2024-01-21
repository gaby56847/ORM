package com.model;

public class CategoryFactory extends TicketCategoryFactory {

  @Override
  public Category createCategory(String name){
      
      //create and return Category object
      Category category = new Category();
      category.setCategorieName(name);
      return category;
      
    }
  
}