import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card, Button, Input } from '@/components/ui';
import { useCourse } from '@/hooks/useCourses';
import { resourceService } from '@/services/api/resource.service';
import { CreateResourceRequest, ResourceType, getResourceTypeDisplayName } from '@/types/resource.types';
import { ROUTES } from '@/router/routes';

export const UploadResourcePage: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const courseId = id ? parseInt(id, 10) : 0;
  const queryClient = useQueryClient();

  // Fetch course details to show course name
  const { data: course, isLoading: courseLoading } = useCourse(courseId);

  const [formData, setFormData] = useState<CreateResourceRequest>({
    courseId: courseId,
    title: '',
    type: ResourceType.EXAM,
    url: '',
    academicYear: '',
    examTerm: '',
  });

  const [errors, setErrors] = useState<Partial<CreateResourceRequest>>({});

  // Update courseId when params change
  useEffect(() => {
    if (courseId) {
      setFormData(prev => ({ ...prev, courseId }));
    }
  }, [courseId]);

  const uploadMutation = useMutation({
    mutationFn: resourceService.createResource,
    onSuccess: () => {
      // Invalidate and refetch relevant queries
      queryClient.invalidateQueries({ queryKey: ['resources'] });
      queryClient.invalidateQueries({ queryKey: ['course-resources'] });

      // Navigate back to course detail page
      navigate(`/courses/${courseId}`);
    },
    onError: (error: any) => {
      setErrors({
        title: error.response?.data?.message || 'Upload failed. Please try again.',
      });
    },
  });

  const validateForm = (): boolean => {
    const newErrors: Partial<CreateResourceRequest> = {};

    if (!formData.courseId || formData.courseId === 0) {
      newErrors.courseId = 1; // Just to indicate error
    }

    if (!formData.title.trim()) {
      newErrors.title = 'Resource title is required';
    }

    if (!formData.url.trim()) {
      newErrors.url = 'Resource URL is required';
    } else {
      // Basic URL validation
      try {
        new URL(formData.url);
      } catch {
        newErrors.url = 'Please enter a valid URL';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) return;

    // Clean up form data
    const submitData: CreateResourceRequest = {
      courseId: formData.courseId,
      title: formData.title.trim(),
      type: formData.type,
      url: formData.url.trim(),
      academicYear: formData.academicYear.trim() || undefined,
      examTerm: formData.examTerm.trim() || undefined,
    };

    uploadMutation.mutate(submitData);
  };

  const handleChange = (field: keyof CreateResourceRequest, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));

    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const academicYears = [
    '2024-2025',
    '2023-2024',
    '2022-2023',
    '2021-2022',
  ];

  const examTerms = [
    'Moed A',
    'Moed B',
    'Moed C',
  ];

  if (courseLoading) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (!course) {
    return (
      <div className="min-h-screen bg-background-soft flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-text-primary mb-2">
            Course Not Found
          </h2>
          <p className="text-text-secondary mb-4">
            The course you're trying to upload to doesn't exist.
          </p>
          <Link to="/courses">
            <Button variant="primary">Back to Courses</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background-soft">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Breadcrumb */}
        <nav className="mb-6">
          <Link
            to={`/courses/${courseId}`}
            className="text-primary hover:text-primary-dark transition-colors"
          >
            ← Back to {course.name}
          </Link>
        </nav>

        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-text-primary mb-2">
            Upload Resource
          </h1>
          <p className="text-text-secondary">
            Share study materials for <span className="font-medium">{course.name}</span>
          </p>
        </div>

        {/* Upload Form */}
        <Card>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Course Display (Read-only) */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Course
              </label>
              <div className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-text-secondary">
                {course.name}
              </div>
            </div>

            {/* Resource Title */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Resource Title *
              </label>
              <Input
                type="text"
                value={formData.title}
                onChange={(e) => handleChange('title', e.target.value)}
                placeholder="e.g., Final Exam 2024, Assignment 3 Solution"
                error={errors.title}
              />
            </div>

            {/* Resource Type */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-3">
                Resource Type *
              </label>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {Object.values(ResourceType).map((type) => (
                  <button
                    key={type}
                    type="button"
                    onClick={() => handleChange('type', type)}
                    className={`p-3 border rounded-lg text-sm font-medium transition-colors ${formData.type === type
                      ? 'border-primary bg-primary text-white'
                      : 'border-gray-300 bg-white text-text-secondary hover:border-primary'
                      }`}
                  >
                    {getResourceTypeDisplayName(type)}
                  </button>
                ))}
              </div>
            </div>

            {/* Resource URL */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Resource URL *
              </label>
              <Input
                type="url"
                value={formData.url}
                onChange={(e) => handleChange('url', e.target.value)}
                placeholder="https://example.com/resource.pdf"
                error={errors.url}
              />
              <p className="mt-1 text-sm text-text-secondary">
                Link to your resource (Google Drive, Dropbox, etc.)
              </p>
            </div>

            {/* Academic Year */}
            <div>
              <label className="block text-sm font-medium text-text-primary mb-2">
                Academic Year (Optional)
              </label>
              <select
                value={formData.academicYear}
                onChange={(e) => handleChange('academicYear', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
              >
                <option value="">Select academic year</option>
                {academicYears.map((year) => (
                  <option key={year} value={year}>
                    {year}
                  </option>
                ))}
              </select>
            </div>

            {/* Exam Term (only for exams) */}
            {formData.type === ResourceType.EXAM && (
              <div>
                <label className="block text-sm font-medium text-text-primary mb-2">
                  Exam Term (Optional)
                </label>
                <select
                  value={formData.examTerm}
                  onChange={(e) => handleChange('examTerm', e.target.value)}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent outline-none transition-colors"
                >
                  <option value="">Select exam term</option>
                  {examTerms.map((term) => (
                    <option key={term} value={term}>
                      {term}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {/* Form Actions */}
            <div className="flex items-center justify-end gap-4 pt-6">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate(`/courses/${courseId}`)}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                variant="primary"
                disabled={uploadMutation.isPending}
              >
                {uploadMutation.isPending ? 'Uploading...' : 'Upload Resource'}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
};