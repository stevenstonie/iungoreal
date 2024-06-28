import { DatePipe } from "@angular/common";
import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
	name: 'timeAgo'
})
export class TimeAgoPipe implements PipeTransform {
	constructor(private datePipe: DatePipe) { }

	transform(value: Date): string {
		if (!(value instanceof Date)) {
			try {
				value = new Date(value);
			} catch (error) {
				return 'Invalid date';
			}
		}

		const timeDifference = new Date().getTime() - value.getTime();
		const seconds = Math.floor(timeDifference / 1000);
		const minutes = Math.floor(seconds / 60);
		const hours = Math.floor(minutes / 60);
		const days = Math.floor(hours / 24);
		const weeks = Math.floor(days / 7);
		const months = Math.floor(days / 30);
		const years = Math.floor(months / 12);

		if (years > 0) {
			return `${years}y ${months % 12}mo ago`;
		} else if (months > 0) {
			return `${months}m ${days % 30}d ago`;
		} else if (weeks > 0) {
			return `${weeks}w ${days % 7}d ago`;
		} else if (days > 0) {
			return `${days}d ${hours % 24}h ago`;
		} else if (hours > 0) {
			return `${hours}h ${minutes % 60}min ago`;
		} else if (minutes > 0) {
			return `${minutes}min ${seconds % 60}s ago`;
		} else if (seconds > 23) {
			return `${seconds}s ago`;
		} else {
			return 'just now';
		}
	}
}