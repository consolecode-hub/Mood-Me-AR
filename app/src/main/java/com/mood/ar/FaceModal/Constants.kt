package com.mood.ar.FaceModal

import com.mood.ar.R


object Constants {
    fun getFaceModelData():ArrayList<FaceModel>{
        // create an arraylist of type FaceModel class
        val FaceModelList=ArrayList<FaceModel>()
        val emp1=FaceModel("f1", R.drawable.f1)
        FaceModelList.add(emp1)
        val emp2=FaceModel("f2",R.drawable.f2)
        FaceModelList.add(emp2)
        val emp3=FaceModel("f3",R.drawable.f3)
        FaceModelList.add(emp3)
        val emp4=FaceModel("f4",R.drawable.f4)
        FaceModelList.add(emp4)
        val emp5=FaceModel("f4",R.drawable.f5)
        FaceModelList.add(emp5)
        val emp6=FaceModel("f5",R.drawable.f6)
        FaceModelList.add(emp6)
        val emp7=FaceModel("f6",R.drawable.f7)
        FaceModelList.add(emp7)
        val emp8=FaceModel("f7",R.drawable.f8)
        FaceModelList.add(emp8)
        val emp9=FaceModel("f8",R.drawable.f9)
        FaceModelList.add(emp9)
        val emp10=FaceModel("f9",R.drawable.f0)
        FaceModelList.add(emp10)

        return  FaceModelList
    }
}