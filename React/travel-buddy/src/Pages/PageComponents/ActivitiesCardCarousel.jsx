/* eslint-disable no-unused-vars */
import  React from "react"

import { Card, CardContent } from "@/components/ui/card"
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"

export const ActivitiesCardCarousel =()=> {
  return (
    <div className="flex gap-5 overflow-hidden">
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
         <Card className="border border-black w-[25rem] h-[15rem]"></Card>
    </div>
  )
}
export default ActivitiesCardCarousel;
